package com.bmk.bmkbookings.util;

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

    @Autowired
    public Scheduler(RestClient restClient){
        this.restClient = restClient;
    }

    public void refreshServices(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("running");
                restClient.getUsers();
                restClient.getServices();
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 20, TimeUnit.MINUTES);
    }
}
