package com.bmk.bmkbookings.util;

import com.bmk.bmkbookings.cache.MerchantCache;
import com.bmk.bmkbookings.cache.ServicesCache;
import com.bmk.bmkbookings.cache.UsersCache;
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
                restClient.getAllMerchants();
                log.info("cache refreshed");
                log.info(ServicesCache.map);
                log.info(UsersCache.map);
                log.info(MerchantCache.map);
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 20, TimeUnit.MINUTES);
    }
}