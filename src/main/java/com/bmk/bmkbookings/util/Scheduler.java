package com.bmk.bmkbookings.util;

import com.bmk.bmkbookings.service.BookingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class Scheduler {

    private static RestClient restClient;
    private static BookingService bookingService;

    @Autowired
    public Scheduler(RestClient restClient, BookingService bookingService){
        this.restClient = restClient;
        this.bookingService = bookingService;
    }

    public void expireOldBookings(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("Checking servers");
                bookingService.expirePastBookings();
                restClient.keepServersAwake();
                log.info("Server Check end");
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 29, TimeUnit.MINUTES);
    }
}
