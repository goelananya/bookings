package com.bmk.bmkbookings.request.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingStatus {
    Long bookingId;
    String status;
}