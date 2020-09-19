package com.bmk.bmkbookings.response.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookingSuccessResponse {
    String responseCode;
    String responseMessage;
    Long bookingId;
}
