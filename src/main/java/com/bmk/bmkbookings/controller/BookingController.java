package com.bmk.bmkbookings.controller;


import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import com.bmk.bmkbookings.response.in.AuthResponse;
import com.bmk.bmkbookings.service.BookingService;
import com.bmk.bmkbookings.util.RestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.ApiTypes;
import constants.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("booking")
@RestController
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;
    private static RestClient restClient;

    @Autowired
    public BookingController(BookingService bookingService, RestClient restClient){
        this.bookingService = bookingService;
        this.restClient = restClient;
    }

    @GetMapping("/merchant")
    public ResponseEntity getBookingsForMerchant(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.gamma.toString();
        Long merchantId = restClient.authorize(token, apiType);
        return ResponseEntity.ok(bookingService.getBookingsForMerchant(merchantId));
    }

    @GetMapping("/client")
    public ResponseEntity getBookingForClient(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.delta.toString();
        Long clientId = restClient.authorize(token, apiType);
        return ResponseEntity.ok(bookingService.getBookingForClient(clientId));
    }

    @GetMapping("/all")
    public ResponseEntity getAllBookings(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.beta.toString();
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/byId")
    public ResponseEntity getBookingById(@RequestHeader String token, @RequestParam Long bookingId) throws UnauthorizedUserException {
        String apiType = ApiTypes.beta.toString();
        restClient.authorize(token, apiType);
        return ResponseEntity.ok(bookingService.findByBookingId(bookingId));
    }

    @PostMapping("createBooking")
    public ResponseEntity createBooking(@RequestHeader String token, @RequestBody String param) throws UnauthorizedUserException, JsonProcessingException {
        String apiType = ApiTypes.delta.toString();
        Long clientId = restClient.authorize(token, apiType);

        Booking booking = new ObjectMapper().readValue(param, Booking.class);
        booking.setClientId(clientId);
        booking.setStatus(BookingStatus.pending.toString());
        booking = bookingService.addNewBooking(booking);
        return ResponseEntity.ok(booking.getBookingId());
    }
}