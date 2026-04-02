package com.example.couple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class CoupleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoupleApplication.class, args);
	}

}
