package com.bmk.bmkbookings;

import com.bmk.bmkbookings.util.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BmkBookingsApplication {

	static Scheduler scheduler;

	@Autowired
	public BmkBookingsApplication(Scheduler scheduler){
		this.scheduler = scheduler;
	}

	public static void main(String[] args) {
		SpringApplication.run(BmkBookingsApplication.class, args);
		scheduler.refreshServices();
	}

}
