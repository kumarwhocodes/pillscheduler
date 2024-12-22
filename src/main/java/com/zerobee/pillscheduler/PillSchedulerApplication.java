package com.zerobee.pillscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.zerobee.pillscheduler.*")
public class PillSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PillSchedulerApplication.class, args);
	}

}
