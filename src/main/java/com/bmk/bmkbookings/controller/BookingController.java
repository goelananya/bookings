package com.bmk.bmkbookings.controller;


import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.bo.Invoice;
import com.bmk.bmkbookings.bo.PaymentBo;
import com.bmk.bmkbookings.exception.*;
import com.bmk.bmkbookings.mapper.BookingsMapper;
import com.bmk.bmkbookings.request.in.UpdateBookingStatus;
import com.bmk.bmkbookings.response.out.*;
import com.bmk.bmkbookings.service.BookingService;
import com.bmk.bmkbookings.service.MerchantUserRelationService;
import com.bmk.bmkbookings.service.PaymentService;
import com.bmk.bmkbookings.util.*;
import com.bmk.bmkbookings.util.RestClient;
import com.bmk.bmkbookings.util.UserType;
import com.fasterxml.jackson.core.JsonProcessingException;
import constants.BookingStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.SignatureException;
import java.util.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RequestMapping("booking")
@RestController
@Log4j2
public class BookingController {

    private final BookingService bookingService;
    private static RestClient restClient;
    private static PaymentService paymentService;
    private static MerchantUserRelationService merchantUserRelationService;
    private static Set<String> statusSet = new HashSet<>();

    @Autowired
    public BookingController(BookingService bookingService, RestClient restClient, PaymentService paymentService, MerchantUserRelationService merchantUserRelationService) {
        this.bookingService = bookingService;
        this.restClient = restClient;
        this.paymentService = paymentService;
        this.merchantUserRelationService = merchantUserRelationService;
        statusSet.add("accepted");
        statusSet.add("denied");
        statusSet.add("completed");
        statusSet.add("cancelled");
    }

    @GetMapping("/merchant")
    public ResponseEntity getBookingsForMerchant(@RequestHeader String token) throws UnauthorizedUserException {
        Long merchantId = restClient.authorize(token, "gamma");
        List<BookingResponse> bookingResponses = BookingsMapper.mapBookings(bookingService.getBookingsForMerchant(merchantId));
        return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingResponses));
    }

    @GetMapping("/client")
    public ResponseEntity getBookingForClient(@RequestHeader String token, Pageable pageable) throws UnauthorizedUserException {
        Long clientId = restClient.authorize(token, "delta");
        Page<Booking> bookingPage = bookingService.getBookingForClient(pageable, clientId);
        List<BookingResponse> bookingResponses = BookingsMapper.mapBookings(bookingPage);
        return ResponseEntity.ok().header("totalElements", Long.toString(bookingPage.getTotalElements())).body(new BookingsListResponse("200", "Success", bookingResponses));
    }

    @GetMapping("/all")
    public ResponseEntity getAllBookings(@RequestHeader String token) throws UnauthorizedUserException {
        restClient.authorize(token, "beta");
        List<BookingResponse> bookingResponses = BookingsMapper.mapBookings(bookingService.getAllBookings());
        return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingResponses));
    }

    @GetMapping("/byId")
    public ResponseEntity getBookingById(@RequestHeader String token, @RequestParam Long bookingId) throws UnauthorizedUserException {
        restClient.authorize(token, "beta");
        return ResponseEntity.ok(new GenericResponse("200", "Success", BookingsMapper.mapBookings(bookingService.findByBookingId(bookingId))));
    }

    @PostMapping("createBooking")
    public ResponseEntity createBooking(@RequestHeader String token, @RequestBody @Valid Booking booking) throws IOException, UnauthorizedUserException, ServiceNotAvailableException, MerchantDoesNotExistException, GenericException, InterruptedException {
        log.info("here");
        booking.setClientId(restClient.authorize(token, "delta"));
        booking.setStatus(BookingStatus.pending.toString());
        //bookingService.addNewBooking(booking);
        restClient.sendBookingNotification(booking);
        restClient.sendEmail(booking);
        updateBillingAmount(booking);
        log.info("End booking method");
        return ResponseEntity.ok(new BookingSuccessResponse("200", "Success", BookingsMapper.mapBooking(booking)));
    }

    @PutMapping("client/updateStatus")
    public ResponseEntity updateBookingStatus(@RequestHeader String token, @RequestBody UpdateBookingStatus bookingStatus) throws InvalidStatusException, UnauthorizedUserException, JsonProcessingException {
        Long clientId = restClient.authorize(token, "delta");
        if (!statusSet.contains(bookingStatus.getStatus())) throw new InvalidStatusException();
        Booking booking = bookingService.findByBookingId(bookingStatus.getBookingId()).iterator().next();
        if (!booking.getClientId().equals(clientId)) throw new UnauthorizedUserException();
        booking.setStatus(bookingStatus.getStatus());
        if(booking.getStatus().equals("completed")) {
            merchantUserRelationService.save(booking);
        }
        bookingService.addNewBooking(booking);
        restClient.sendStatusUpdateNotification(booking, UserType.merchant);
        return ResponseEntity.ok(new GenericResponse("200", "Success", BookingsMapper.mapBooking(booking)));
    }

    @PutMapping("merchant/updateStatus")
    public ResponseEntity updateBooking(@RequestHeader String token, @RequestBody UpdateBookingStatus bookingStatus) throws InvalidStatusException, UnauthorizedUserException, JsonProcessingException {
        Long merchantId = restClient.authorize(token, "gamma");
        if (!statusSet.contains(bookingStatus.getStatus())) throw new InvalidStatusException();
        Booking booking = bookingService.findByBookingId(bookingStatus.getBookingId()).iterator().next();
        if (!booking.getMerchantId().equals(merchantId)) throw new UnauthorizedUserException();
        booking.setStatus(bookingStatus.getStatus());
        if(booking.getStatus().equals("completed")) {
            merchantUserRelationService.save(booking);
        }
        bookingService.addNewBooking(booking);
        restClient.sendStatusUpdateNotification(booking, UserType.client);
        return ResponseEntity.ok(new GenericResponse("200", "Success", BookingsMapper.mapBooking(booking)));
    }

    @PostMapping("payment")
    public ResponseEntity payment(@RequestHeader String token, @RequestBody PaymentBo paymentBo) throws UnauthorizedUserException, SignatureException {
        restClient.authorize(token, "delta");
        paymentService.addPayment(paymentBo);
        String generated_signature = new Signature().calculateRFC2104HMAC(paymentBo.getOrderId() + "|" + paymentBo.getRazorpay_payment_id(), System.getenv("rpSec"));

        if (generated_signature.equals(paymentBo.getRazorpay_signature())) {
            return ResponseEntity.ok(new GenericResponse("200", "Success", "Received Payment"));
        } else {
            return ResponseEntity.badRequest().body(new GenericResponse("400", "Failed", "No Payment Received"));
        }

    }

    @GetMapping("rel")
    public ResponseEntity getMerchantClientBookings(@RequestHeader String token, @RequestParam Long clientId) throws UnauthorizedUserException {
        Long merchantId = restClient.authorize(token, "gamma");
        List<Booking> bookings = bookingService.findByMerchantIdAndClientId(merchantId, clientId);
        return ResponseEntity.ok(new GenericResponse("200", "Success", bookings.stream().map(BookingsMapper::mapBookingLite)));
    }


    @GetMapping("ping")
    public String ping() {
        return "Hello";
    }

    @Async
    public void updateBillingAmount(Booking booking) throws ServiceNotAvailableException, MerchantDoesNotExistException {
        Long start = System.currentTimeMillis();
        Invoice invoice = Helper.getInvoice(restClient.getPortfolio(booking.getMerchantId()), booking.getServiceIdCsv());
        booking.setPayableAmount((int) invoice.getTotalAmount() * 100);
        String razorpayCreateOrderId = restClient.createOrder(booking.getPayableAmount(), 0L).getId();
        booking.setRazorpayOrderId(razorpayCreateOrderId);
        invoice.setInvoiceId(razorpayCreateOrderId);
        bookingService.addNewBooking(booking);
        Long end = System.currentTimeMillis();
        log.info("Total time for updateBillingAmount:"+(end-start));
    }
}