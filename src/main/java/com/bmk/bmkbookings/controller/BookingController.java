package com.bmk.bmkbookings.controller;


import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.bo.Invoice;
import com.bmk.bmkbookings.bo.PaymentBo;
import com.bmk.bmkbookings.exception.InvalidStatusException;
import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import com.bmk.bmkbookings.request.in.UpdateBookingStatus;
import com.bmk.bmkbookings.response.in.PortfolioResponse;
import com.bmk.bmkbookings.response.in.RazorpayCreateOrderId;
import com.bmk.bmkbookings.response.out.BookingSuccessResponse;
import com.bmk.bmkbookings.response.out.BookingsListResponse;
import com.bmk.bmkbookings.response.out.ErrorResponse;
import com.bmk.bmkbookings.response.out.GenericResponse;
import com.bmk.bmkbookings.service.BookingService;
import com.bmk.bmkbookings.service.PaymentService;
import com.bmk.bmkbookings.util.Helper;
import com.bmk.bmkbookings.util.RestClient;
import com.bmk.bmkbookings.util.Signature;
import com.bmk.bmkbookings.util.UserType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.ApiTypes;
import constants.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;
import java.util.HashSet;
import java.util.Set;

@RequestMapping("booking")
@RestController
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;
    private static RestClient restClient;
    private static PaymentService paymentService;
    private static Set<String> statusSet = new HashSet<>();

    @Autowired
    public BookingController(BookingService bookingService, RestClient restClient, PaymentService paymentService){
        this.bookingService = bookingService;
        this.restClient = restClient;
        this.paymentService = paymentService;
        statusSet.add("cancel");
        statusSet.add("approve");
        statusSet.add("deny");
        statusSet.add("completed");
    }

    @GetMapping("/merchant")
    public ResponseEntity getBookingsForMerchant(@RequestHeader String token){
        String apiType = ApiTypes.gamma.toString();
        try {
            Long merchantId = restClient.authorize(token, apiType);
            return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingService.getBookingsForMerchant(merchantId)));
        } catch (UnauthorizedUserException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
        }
    }

    @GetMapping("/client")
    public ResponseEntity getBookingForClient(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.delta.toString();
        try {
            Long clientId = restClient.authorize(token, apiType);
            return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingService.getBookingForClient(clientId)));
        } catch (UnauthorizedUserException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity getAllBookings(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.beta.toString();
        try {
            restClient.authorize(token, apiType);
            return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingService.getAllBookings()));
        } catch (UnauthorizedUserException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
        }
    }

    @GetMapping("/byId")
    public ResponseEntity getBookingById(@RequestHeader String token, @RequestParam Long bookingId) throws UnauthorizedUserException {
        String apiType = ApiTypes.beta.toString();
        try {
            restClient.authorize(token, apiType);
            return ResponseEntity.ok(new GenericResponse("200", "Success", bookingService.findByBookingId(bookingId)));
        } catch (UnauthorizedUserException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
        }
    }

    @PostMapping("createBooking")
    public ResponseEntity createBooking(@RequestHeader String token, @RequestBody String param) throws UnauthorizedUserException, JsonProcessingException {
        String apiType = ApiTypes.delta.toString();
        try {
            Booking booking = new ObjectMapper().readValue(param, Booking.class);
            booking.setClientId(restClient.authorize(token, apiType));
            booking.setStatus(BookingStatus.pending.toString());
            booking = bookingService.addNewBooking(booking);

            Invoice invoice = Helper.getInvoice(restClient.getPortfolio(booking.getMerchantId()), booking.getServiceIdCsv());
            String razorpayCreateOrderId = restClient.createOrder((int)(invoice.getTotalAmount()*100), booking.getBookingId()).getId();
            booking.setRazorpayOrderId(razorpayCreateOrderId);
            invoice.setInvoiceId(razorpayCreateOrderId);
            booking = bookingService.addNewBooking(booking);
            restClient.sendBookingNotification(booking);
            return ResponseEntity.ok(new BookingSuccessResponse("200", "Success", invoice));
        }catch(UnauthorizedUserException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("400", e.getMessage()));
            }
    }

    @PutMapping("client/updateStatus")
    public ResponseEntity updateBookingStatus(@RequestHeader String token, @RequestBody String param){
        String apiType = ApiTypes.delta.toString();
        try {
            Long clientId = restClient.authorize(token, apiType);
            UpdateBookingStatus bookingStatus = new ObjectMapper().readValue(param, UpdateBookingStatus.class);
            if(!statusSet.contains(bookingStatus.getStatus())) throw new InvalidStatusException();
            Booking booking = bookingService.findByBookingId(bookingStatus.getBookingId()).iterator().next();
            if(!booking.getClientId().equals(clientId))  throw new UnauthorizedUserException();
            booking.setStatus(bookingStatus.getStatus());
            bookingService.addNewBooking(booking);
            restClient.sendStatusUpdateNotification(booking, UserType.merchant);
            return ResponseEntity.ok(new GenericResponse("200", "Success", booking));
        } catch (InvalidStatusException| UnauthorizedUserException| JsonProcessingException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("400", e.getMessage()));
        }
    }

    @PutMapping("merchant/updateStatus")
    public ResponseEntity updateBooking(@RequestHeader String token, @RequestBody String param){
        String apiType = ApiTypes.gamma.toString();
        try {
            Long merchantId = restClient.authorize(token, apiType);
            UpdateBookingStatus bookingStatus = new ObjectMapper().readValue(param, UpdateBookingStatus.class);
            if(!statusSet.contains(bookingStatus.getStatus())) throw new InvalidStatusException();
            Booking booking = bookingService.findByBookingId(bookingStatus.getBookingId()).iterator().next();
            if(!booking.getMerchantId().equals(merchantId)) throw new UnauthorizedUserException();
            booking.setStatus(bookingStatus.getStatus());
            bookingService.addNewBooking(booking);
            restClient.sendStatusUpdateNotification(booking, UserType.client);
            return ResponseEntity.ok(new GenericResponse("200", "Success", booking));
        } catch (InvalidStatusException| UnauthorizedUserException| JsonProcessingException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("400", e.getMessage()));
        }
    }

    @PostMapping("payment")
    public ResponseEntity payment(@RequestHeader String token,@RequestBody String param) throws UnauthorizedUserException, JsonProcessingException, SignatureException {
        restClient.authorize(token, "delta");
        PaymentBo paymentBo = new ObjectMapper().readValue(param, PaymentBo.class);
        paymentService.addPayment(paymentBo);
        String generated_signature = new Signature().calculateRFC2104HMAC(paymentBo.getOrderId() + "|" + paymentBo.getRazorpay_payment_id(), "1UlbCzEnbK07ok9XkAgNYYJI");

        if (generated_signature.equals(paymentBo.getRazorpay_signature())) {
            System.out.println("Payment Success");
        }       else{
            System.out.println("Invalid signature");
        }
        return null;
    }
}