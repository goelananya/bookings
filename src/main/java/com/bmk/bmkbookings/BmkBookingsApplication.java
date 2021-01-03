package com.bmk.bmkbookings;

import com.bmk.bmkbookings.util.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class BmkBookingsApplication {

	static Scheduler scheduler;

	@Autowired
	public BmkBookingsApplication(Scheduler scheduler){
		this.scheduler = scheduler;
	}

	public static void main(String[] args) {
		SpringApplication.run(BmkBookingsApplication.class, args);
		scheduler.expireOldBookings();
	}

}
