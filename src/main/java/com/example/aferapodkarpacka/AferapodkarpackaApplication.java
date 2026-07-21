package com.example.aferapodkarpacka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AferapodkarpackaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AferapodkarpackaApplication.class, args);
	}
}