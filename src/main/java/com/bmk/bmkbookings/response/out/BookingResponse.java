package com.bmk.bmkbookings.response.out;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.response.in.Service;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BookingResponse {
    Long bookingId;
    String clientName;
    MerchantResponse merchant;
    List<Service> services;
    Date date;
    String status;
    String razorpayOrderId;
    int payableAmount;

    public BookingResponse(Booking booking){
        this.bookingId = booking.getBookingId();
        this.date = booking.getDate();
        this.status = booking.getStatus();
        this.razorpayOrderId = booking.getRazorpayOrderId();
        this.payableAmount = booking.getPayableAmount();
    }
}