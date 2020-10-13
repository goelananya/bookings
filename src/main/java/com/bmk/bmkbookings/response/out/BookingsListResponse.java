package com.bmk.bmkbookings.response.out;

import com.bmk.bmkbookings.bo.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class BookingsListResponse {
    String responseCode;
    String message;
    List<BookingResponse> bookings;
}
