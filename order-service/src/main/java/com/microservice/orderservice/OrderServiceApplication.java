package com.microservice.orderservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class OrderServiceApplication {

	public static void main(String[] args) {
		String springProfileActive = "local";
		Map<String, String> envVariables = System.getenv();
		if(envVariables.containsKey("PROFILE_ACTIVE")){
			springProfileActive = envVariables.get("PROFILE_ACTIVE");

			System.out.println("PROFILE_ACTIVE Value: " + springProfileActive);

			// System.setProperty("spring.profiles.active",springProfileActive);
			// System.out.println(System.getProperty("spring.profiles.active"));
		} else {
			System.out.println("PROFILE_ACTIVE variable is not set.");
		}
		new SpringApplicationBuilder(OrderServiceApplication.class)
				.profiles(springProfileActive)
				.run(args);
	}

	@Component
	class MyRunner implements CommandLineRunner {

		@Autowired
		private Environment environment;

		@Override
		public void run(String... args) throws Exception {

			log.info("Active profiles: " +
					Arrays.toString(environment.getActiveProfiles()));
		}
	}

	@Component
	@Profile(value="dev")
	class MyRunner2 implements CommandLineRunner {

		@Override
		public void run(String... args) throws Exception {

			log.info("In development");
		}
	}

	@Component
	@Profile(value="prod & !dev")
	class MyRunner3 implements CommandLineRunner {

		@Override
		public void run(String... args) throws Exception {

			log.info("In production");
		}
	}

	@Component
	@Profile(value="local")
	class MyRunner4 implements CommandLineRunner {

		@Override
		public void run(String... args) throws Exception {

			log.info("In local");
		}
	}

	@Component
	@Profile(value={"dev & local"})
	class MyRunner5 implements CommandLineRunner {

		@Override
		public void run(String... args) throws Exception {

			log.info("In development and local");
		}
	}

	@Component
	@Profile(value={"dev", "prod"})
	class MyRunner6 implements CommandLineRunner {

		@Value("${message}")
		private String message;

		@Override
		public void run(String... args) throws Exception {

			log.info("Message: " + message);
		}
	}

}
