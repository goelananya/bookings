package com.bmk.bmkbookings.repo;

import com.bmk.bmkbookings.bo.Booking;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface BookingRepo extends CrudRepository<Booking, Long> {
    Booking[] findByMerchantId(Long merchantId);
    Booking[] findByClientId(Long clientId);
    Booking[] findAllByDateAfterAndDateBefore(Date minDate, Date maxDate);
    Booking[] findByBookingId(Long bookingId);
}