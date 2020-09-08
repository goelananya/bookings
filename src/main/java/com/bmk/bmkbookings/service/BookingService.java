package com.bmk.bmkbookings.service;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.repo.BookingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.Date;

@Service
public class BookingService {

    public final BookingRepo bookingRepo;

    @Autowired
    public BookingService(BookingRepo bookingRepo){
        this.bookingRepo = bookingRepo;
    }

    public Booking addNewBooking(Booking booking){
        return bookingRepo.save(booking);
    }

    public Booking[] getBookingForClient(Long clientId){
        return bookingRepo.findByClientId(clientId);
    }

    public Booking[] getBookingsForMerchant(Long merchantId){
        return bookingRepo.findByMerchantId(merchantId);
    }

    public Iterable<Booking> getAllBookings(){
        return bookingRepo.findAll();
    }

    public Booking[] getBookingsInDateRange(Date minDate, Date maxDate){
        return bookingRepo.findAllByDateAfterAndDateBefore(minDate, maxDate);
    }

    public Booking[] findByBookingId(Long bookingId){
        return bookingRepo.findByBookingId(bookingId);
    }
}