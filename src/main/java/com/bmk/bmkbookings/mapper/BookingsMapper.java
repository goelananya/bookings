package com.bmk.bmkbookings.mapper;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.response.in.Service;
import com.bmk.bmkbookings.response.out.BookingResponse;
import com.bmk.bmkbookings.util.RestClient;
import com.bmk.bmkbookings.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class BookingsMapper {

    static RestClient restClient;

    @Autowired
    public BookingsMapper(RestClient restClient) {
        this.restClient = restClient;
    }

    public static List<BookingResponse> mapBookings(Iterable<Booking> bookings) {

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for(Booking booking: bookings){
            bookingResponses.add(mapBooking(booking));
        }

    return bookingResponses;
    }


    public static BookingResponse mapBooking(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse(booking);
        bookingResponse.setClientName(restClient.getUser(booking.getClientId()).getName());
        bookingResponse.setMerchant(restClient.getMerchantById(booking.getMerchantId()));
        List<Service> services = new ArrayList<>();
        for(String s: booking.getServiceIdCsv().split(",")) {
            if(StringUtil.isEmpty(s))   break;
            Long serviceId = Long.parseLong(s);
            services.add(restClient.getServices(serviceId));
        }
        bookingResponse.setServices(services);
        return bookingResponse;
    }
}
