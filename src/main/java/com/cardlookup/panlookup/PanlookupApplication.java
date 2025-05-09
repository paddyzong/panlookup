package com.cardlookup.panlookup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PanlookupApplication {

	public static void main(String[] args) {
		SpringApplication.run(PanlookupApplication.class, args);
	}

}
