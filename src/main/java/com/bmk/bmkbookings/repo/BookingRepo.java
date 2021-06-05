package com.bmk.bmkbookings.repo;

import com.bmk.bmkbookings.bo.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface BookingRepo extends CrudRepository<Booking, Long> {
    Iterable<Booking> findByMerchantId(Long merchantId);
    Page<Booking> findByClientId(Pageable pageable, Long clientId);
    Iterable<Booking> findAllByDateAfterAndDateBefore(Date minDate, Date maxDate);
    Iterable<Booking> findByBookingId(Long bookingId);
    List<Booking> findAllByStatus(String bookingStatus);
}