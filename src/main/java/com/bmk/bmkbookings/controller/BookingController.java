package com.bmk.bmkbookings.controller;


import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.bo.Invoice;
import com.bmk.bmkbookings.bo.PaymentBo;
import com.bmk.bmkbookings.enums.BookingStatusEnum;
import com.bmk.bmkbookings.exception.InvalidStatusException;
import com.bmk.bmkbookings.exception.ServiceNotAvailableException;
import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import com.bmk.bmkbookings.mapper.BookingsMapper;
import com.bmk.bmkbookings.request.in.UpdateBookingStatus;
import com.bmk.bmkbookings.response.out.*;
import com.bmk.bmkbookings.service.BookingService;
import com.bmk.bmkbookings.service.PaymentService;
import com.bmk.bmkbookings.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import constants.ApiTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public BookingController(BookingService bookingService, RestClient restClient, PaymentService paymentService) {
        this.bookingService = bookingService;
        this.restClient = restClient;
        this.paymentService = paymentService;
    }

    @GetMapping("/merchant")
    public ResponseEntity getBookingsForMerchant(@RequestHeader String token) throws UnauthorizedUserException {
        Long merchantId = restClient.authorize(token, ApiTypes.gamma.toString());
        List<BookingResponse> bookingResponses = BookingsMapper.mapBookings(bookingService.getBookingsForMerchant(merchantId));
        return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingResponses));
    }

    @GetMapping("/client")
    public ResponseEntity getBookingForClient(@RequestHeader String token) throws UnauthorizedUserException {
        Long clientId = restClient.authorize(token, ApiTypes.delta.toString());
        List<BookingResponse> bookingResponses = BookingsMapper.mapBookings(bookingService.getBookingForClient(clientId));
        return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingResponses));
    }

    @GetMapping("/all")
    public ResponseEntity getAllBookings(@RequestHeader String token) throws UnauthorizedUserException {
        restClient.authorize(token, ApiTypes.beta.toString());
        List<BookingResponse> bookingResponses = BookingsMapper.mapBookings(bookingService.getAllBookings());
        return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingResponses));
    }

    @GetMapping("/byId")
    public ResponseEntity getBookingById(@RequestHeader String token, @RequestParam Long bookingId) throws UnauthorizedUserException {
        restClient.authorize(token, ApiTypes.beta.toString());
        return ResponseEntity.ok(new GenericResponse("200", "Success", BookingsMapper.mapBookings(bookingService.findByBookingId(bookingId))));
    }

    @PostMapping("createBooking")
    public ResponseEntity createBooking(@RequestHeader String token, @RequestBody Booking booking) throws JsonProcessingException, UnauthorizedUserException, ServiceNotAvailableException, InvalidStatusException {
        booking.setClientId(restClient.authorize(token, ApiTypes.delta.toString()));
        booking.setStatus(BookingStatusEnum.constructObjectFromValue("pending"));
        booking = bookingService.addNewBooking(booking);
        updateBillingAmount(booking);
        restClient.sendBookingNotification(booking);
        return ResponseEntity.ok(new BookingSuccessResponse("200", "Success", BookingsMapper.mapBooking(booking)));
    }

    @PutMapping("client/updateStatus")
    public ResponseEntity updateBookingStatus(@RequestHeader String token, @RequestBody UpdateBookingStatus bookingStatus) throws InvalidStatusException, UnauthorizedUserException, JsonProcessingException {
        Long clientId = restClient.authorize(token, ApiTypes.delta.toString());
        Booking booking = bookingService.findByBookingId(bookingStatus.getBookingId()).iterator().next();
        if (!booking.getClientId().equals(clientId)) throw new UnauthorizedUserException();
        booking.setStatus(BookingStatusEnum.constructObjectFromValue(bookingStatus.getStatus()));
        bookingService.addNewBooking(booking);
        restClient.sendStatusUpdateNotification(booking, UserType.merchant);
        return ResponseEntity.ok(new GenericResponse("200", "Success", BookingsMapper.mapBooking(booking)));
    }

    @PutMapping("merchant/updateStatus")
    public ResponseEntity updateBooking(@RequestHeader String token, @RequestBody UpdateBookingStatus bookingStatus) throws InvalidStatusException, UnauthorizedUserException, JsonProcessingException {
        Long merchantId = restClient.authorize(token, ApiTypes.gamma.toString());
        Booking booking = bookingService.findByBookingId(bookingStatus.getBookingId()).iterator().next();
        if (!booking.getMerchantId().equals(merchantId)) throw new UnauthorizedUserException();
        booking.setStatus(BookingStatusEnum.constructObjectFromValue(bookingStatus.getStatus()));
        bookingService.addNewBooking(booking);
        restClient.sendStatusUpdateNotification(booking, UserType.client);
        return ResponseEntity.ok(new GenericResponse("200", "Success", BookingsMapper.mapBooking(booking)));
    }

    @PostMapping("payment")
    public ResponseEntity payment(@RequestHeader String token, @RequestBody PaymentBo paymentBo) throws UnauthorizedUserException, JsonProcessingException, SignatureException {
        restClient.authorize(token, "delta");
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