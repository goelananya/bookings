package com.bmk.bmkbookings.service;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.repo.BookingRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class BookingService {

    public final BookingRepo bookingRepo;

    @Autowired
    public BookingService(BookingRepo bookingRepo){
        this.bookingRepo = bookingRepo;
    }

    @Async
    public void addNewBooking(Booking booking){
        booking.setBookingId(bookingRepo.save(booking).getBookingId());
    }

    public Page<Booking> getBookingForClient(Pageable pageable, Long clientId){
        return bookingRepo.findByClientId(pageable, clientId);
    }

    public Iterable<Booking> getBookingsForMerchant(Long merchantId){
        return bookingRepo.findByMerchantId(merchantId);
    }

    public Iterable<Booking> getAllBookings(){
        return bookingRepo.findAll();
    }

    public Iterable<Booking> getBookingsInDateRange(Date minDate, Date maxDate){
        return bookingRepo.findAllByDateAfterAndDateBefore(minDate, maxDate);
    }

    public Iterable<Booking> findByBookingId(Long bookingId){
        return bookingRepo.findByBookingId(bookingId);
    }

    public void expirePastBookings() {
        log.info("Calling db from expire bookings method");
        List<Booking> bookings = bookingRepo.findAllByStatus("pending");
        log.info("Pending bookings:"+bookings);
        Date currentDate = new Date();
        for (int i = 0; i<bookings.size(); i++) {
            if(bookings.get(i).getDate().before(currentDate)) {
                Booking booking = bookings.get(i);
                log.info("Moving: "+booking+" to expired");
                booking.setStatus("expired");
                bookings.set(i, booking);
            }
        }
        bookingRepo.saveAll(bookings);
    }
}