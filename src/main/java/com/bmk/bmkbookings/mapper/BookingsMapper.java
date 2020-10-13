package com.bmk.bmkbookings.mapper;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.cache.ServicesCache;
import com.bmk.bmkbookings.cache.UsersCache;
import com.bmk.bmkbookings.response.in.Service;
import com.bmk.bmkbookings.response.in.User;
import com.bmk.bmkbookings.response.out.BookingResponse;
import com.bmk.bmkbookings.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingsMapper {

    static Map<Long, User> usersCache = UsersCache.map;
    static Map<Long, Service> servicesCache = ServicesCache.map;

    public static List<BookingResponse> mapBookings(Iterable<Booking> bookings) {

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for(Booking booking: bookings){
            bookingResponses.add(mapBooking(booking));
        }

    return bookingResponses;
    }


    public static BookingResponse mapBooking(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse(booking);
        bookingResponse.setClientName(usersCache.get(booking.getClientId()).getName());
        bookingResponse.setMerchantName(usersCache.get(booking.getMerchantId()).getName());
        List<Service> services = new ArrayList<>();
        for(String s: booking.getServiceIdCsv().split(",")) {
            if(StringUtil.isEmpty(s))   break;
            Long serviceId = Long.parseLong(s);
            services.add(servicesCache.get(serviceId));
        }
        bookingResponse.setServices(services);
        return bookingResponse;
    }

}
