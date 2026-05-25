package com.example.Family_life_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FamilyLifeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FamilyLifeBackendApplication.class, args);
	}

}
