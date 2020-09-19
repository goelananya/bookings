package com.bmk.bmkbookings.repo;

import com.bmk.bmkbookings.bo.Booking;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface BookingRepo extends CrudRepository<Booking, Long> {
    Iterable<Booking> findByMerchantId(Long merchantId);
    Iterable<Booking> findByClientId(Long clientId);
    Iterable<Booking> findAllByDateAfterAndDateBefore(Date minDate, Date maxDate);
    Iterable<Booking> findByBookingId(Long bookingId);
}