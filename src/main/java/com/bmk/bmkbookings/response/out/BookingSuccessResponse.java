package com.bmk.bmkbookings.response.out;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.bo.Invoice;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookingSuccessResponse {
    String responseCode;
    String responseMessage;
    BookingResponse booking;
}
