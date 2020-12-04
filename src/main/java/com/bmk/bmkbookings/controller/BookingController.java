package com.bmk.bmkbookings.controller;


import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.bo.Invoice;
import com.bmk.bmkbookings.bo.PaymentBo;
import com.bmk.bmkbookings.cache.ServicesCache;
import com.bmk.bmkbookings.cache.UsersCache;
import com.bmk.bmkbookings.exception.InvalidStatusException;
import com.bmk.bmkbookings.exception.ServiceNotAvailableException;
import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import com.bmk.bmkbookings.mapper.BookingsMapper;
import com.bmk.bmkbookings.request.in.UpdateBookingStatus;
import com.bmk.bmkbookings.response.in.PortfolioResponse;
import com.bmk.bmkbookings.response.in.RazorpayCreateOrderId;
import com.bmk.bmkbookings.response.in.Service;
import com.bmk.bmkbookings.response.in.User;
import com.bmk.bmkbookings.response.out.*;
import com.bmk.bmkbookings.service.BookingService;
import com.bmk.bmkbookings.service.PaymentService;
import com.bmk.bmkbookings.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.ApiTypes;
import constants.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;
import java.util.*;

@RequestMapping("booking")
@RestController
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;
    private static RestClient restClient;
    private static PaymentService paymentService;
    private static Set<String> statusSet = new HashSet<>();

    @Autowired
    public BookingController(BookingService bookingService, RestClient restClient, PaymentService paymentService) {
        this.bookingService = bookingService;
        this.restClient = restClient;
        this.paymentService = paymentService;
        statusSet.add("accepted");
        statusSet.add("denied");
        statusSet.add("completed");
    }

    @GetMapping("/merchant")
    public ResponseEntity getBookingsForMerchant(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.gamma.toString();
        Long merchantId = restClient.authorize(token, apiType);
        List<BookingResponse> bookingResponses = BookingsMapper.mapBookings(bookingService.getBookingsForMerchant(merchantId));
        return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingResponses));
    }

    @GetMapping("/client")
    public ResponseEntity getBookingForClient(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.delta.toString();
        Long clientId = restClient.authorize(token, apiType);
        List<BookingResponse> bookingResponses = BookingsMapper.mapBookings(bookingService.getBookingForClient(clientId));
        return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingResponses));
    }

    @GetMapping("/all")
    public ResponseEntity getAllBookings(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.beta.toString();
        restClient.authorize(token, apiType);
        List<BookingResponse> bookingResponses = BookingsMapper.mapBookings(bookingService.getAllBookings());
        return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingResponses));
    }

    @GetMapping("/byId")
    public ResponseEntity getBookingById(@RequestHeader String token, @RequestParam Long bookingId) throws UnauthorizedUserException {
        String apiType = ApiTypes.beta.toString();
        restClient.authorize(token, apiType);
        return ResponseEntity.ok(new GenericResponse("200", "Success", BookingsMapper.mapBookings(bookingService.findByBookingId(bookingId))));
    }

    @PostMapping("createBooking")
    public ResponseEntity createBooking(@RequestHeader String token, @RequestBody String param) throws JsonProcessingException, UnauthorizedUserException, ServiceNotAvailableException {
        String apiType = ApiTypes.delta.toString();
        Booking booking = new ObjectMapper().readValue(param, Booking.class);
        booking.setClientId(restClient.authorize(token, apiType));
        booking.setStatus(BookingStatus.pending.toString());
        booking = bookingService.addNewBooking(booking);
        updateBillingAmount(booking);
        restClient.sendBookingNotification(booking);
        return ResponseEntity.ok(new BookingSuccessResponse("200", "Success", BookingsMapper.mapBooking(booking)));
    }

    @PutMapping("client/updateStatus")
    public ResponseEntity updateBookingStatus(@RequestHeader String token, @RequestBody String param) throws InvalidStatusException, UnauthorizedUserException, JsonProcessingException {
        String apiType = ApiTypes.delta.toString();
        Long clientId = restClient.authorize(token, apiType);
        UpdateBookingStatus bookingStatus = new ObjectMapper().readValue(param, UpdateBookingStatus.class);
        if (!statusSet.contains(bookingStatus.getStatus())) throw new InvalidStatusException();
        Booking booking = bookingService.findByBookingId(bookingStatus.getBookingId()).iterator().next();
        if (!booking.getClientId().equals(clientId)) throw new UnauthorizedUserException();
        booking.setStatus(bookingStatus.getStatus());
        bookingService.addNewBooking(booking);
        restClient.sendStatusUpdateNotification(booking, UserType.merchant);
        return ResponseEntity.ok(new GenericResponse("200", "Success", BookingsMapper.mapBooking(booking)));
    }

    @PutMapping("merchant/updateStatus")
    public ResponseEntity updateBooking(@RequestHeader String token, @RequestBody String param) throws InvalidStatusException, UnauthorizedUserException, JsonProcessingException {
        String apiType = ApiTypes.gamma.toString();
        Long merchantId = restClient.authorize(token, apiType);
        UpdateBookingStatus bookingStatus = new ObjectMapper().readValue(param, UpdateBookingStatus.class);
        if (!statusSet.contains(bookingStatus.getStatus())) throw new InvalidStatusException();
        Booking booking = bookingService.findByBookingId(bookingStatus.getBookingId()).iterator().next();
        if (!booking.getMerchantId().equals(merchantId)) throw new UnauthorizedUserException();
        booking.setStatus(bookingStatus.getStatus());
        bookingService.addNewBooking(booking);
        restClient.sendStatusUpdateNotification(booking, UserType.client);
        return ResponseEntity.ok(new GenericResponse("200", "Success", BookingsMapper.mapBooking(booking)));
    }

    @PostMapping("payment")
    public ResponseEntity payment(@RequestHeader String token, @RequestBody String param) throws UnauthorizedUserException, JsonProcessingException, SignatureException {
        restClient.authorize(token, "delta");
        PaymentBo paymentBo = new ObjectMapper().readValue(param, PaymentBo.class);
        paymentService.addPayment(paymentBo);
        String generated_signature = new Signature().calculateRFC2104HMAC(paymentBo.getOrderId() + "|" + paymentBo.getRazorpay_payment_id(), "1UlbCzEnbK07ok9XkAgNYYJI");

        if (generated_signature.equals(paymentBo.getRazorpay_signature())) {
            return ResponseEntity.ok(new GenericResponse("200", "Success", "Received Payment"));
        } else {
            return ResponseEntity.badRequest().body(new GenericResponse("400", "Failed", "No Payment Received"));
        }

    }

    @GetMapping("ping")
    public String ping() {
        return "Hello";
    }

    @Async
    public void updateBillingAmount(Booking booking) throws ServiceNotAvailableException {
        Invoice invoice = Helper.getInvoice(restClient.getPortfolio(booking.getMerchantId()), booking.getServiceIdCsv());
        booking.setPayableAmount((int) invoice.getTotalAmount() * 100);
        String razorpayCreateOrderId = restClient.createOrder(booking.getPayableAmount(), booking.getBookingId()).getId();
        booking.setRazorpayOrderId(razorpayCreateOrderId);
        invoice.setInvoiceId(razorpayCreateOrderId);
        bookingService.addNewBooking(booking);
    }

}