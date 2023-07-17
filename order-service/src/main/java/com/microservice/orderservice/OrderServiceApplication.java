package com.microservice.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		Map<String, String> envVariables = System.getenv();
		if(envVariables.containsKey("PROFILE_ACTIVE")){
			String springProfileActive = envVariables.get("PROFILE_ACTIVE");

			System.out.println("PROFILE_ACTIVE Value: " + springProfileActive);

			System.setProperty("spring.profiles.active",springProfileActive);
		} else {
			System.out.println("PROFILE_ACTIVE variable is not set.");
		}
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
