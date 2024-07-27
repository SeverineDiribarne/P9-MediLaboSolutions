package com.medilabo.medilabo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class MedilaboApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedilaboApplication.class, args);
	}

}
