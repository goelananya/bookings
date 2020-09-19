package com.bmk.bmkbookings.controller;


import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import com.bmk.bmkbookings.response.out.BookingSuccessResponse;
import com.bmk.bmkbookings.response.out.BookingsListResponse;
import com.bmk.bmkbookings.response.out.ErrorResponse;
import com.bmk.bmkbookings.response.out.GenericResponse;
import com.bmk.bmkbookings.service.BookingService;
import com.bmk.bmkbookings.util.RestClient;
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
        try {
            Long merchantId = restClient.authorize(token, apiType);
            return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingService.getBookingsForMerchant(merchantId)));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponse("403", "Unknown error encountered"));
        }
    }

    @GetMapping("/client")
    public ResponseEntity getBookingForClient(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.delta.toString();
        try {
            Long clientId = restClient.authorize(token, apiType);
            return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingService.getBookingForClient(clientId)));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponse("403", "Unknown error encountered"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity getAllBookings(@RequestHeader String token) throws UnauthorizedUserException {
        String apiType = ApiTypes.beta.toString();
        try {
            restClient.authorize(token, apiType);
            return ResponseEntity.ok(new BookingsListResponse("200", "Success", bookingService.getAllBookings()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponse("403", "Unknown error encountered"));
        }
    }

    @GetMapping("/byId")
    public ResponseEntity getBookingById(@RequestHeader String token, @RequestParam Long bookingId) throws UnauthorizedUserException {
        String apiType = ApiTypes.beta.toString();
        try {
            restClient.authorize(token, apiType);
            return ResponseEntity.ok(new GenericResponse("200", "Success", bookingService.findByBookingId(bookingId)));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponse("403", "Unknown error encountered"));
        }
    }

    @PostMapping("createBooking")
    public ResponseEntity createBooking(@RequestHeader String token, @RequestBody String param) throws UnauthorizedUserException, JsonProcessingException {
        String apiType = ApiTypes.delta.toString();
        try {
            Long clientId = restClient.authorize(token, apiType);

            Booking booking = new ObjectMapper().readValue(param, Booking.class);
            booking.setClientId(clientId);
            booking.setStatus(BookingStatus.pending.toString());
            booking = bookingService.addNewBooking(booking);
            return ResponseEntity.ok(new BookingSuccessResponse("200", "Success", booking.getBookingId()));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponse("403", "Unknown error encountered"));
        }
    }
}