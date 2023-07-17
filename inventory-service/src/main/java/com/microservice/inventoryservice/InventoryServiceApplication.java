package com.microservice.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {

		Map<String, String> envVariables = System.getenv();
		if(envVariables.containsKey("PROFILE_ACTIVE")){
			String springProfileActive = envVariables.get("PROFILE_ACTIVE");

			System.out.println("PROFILE_ACTIVE Value: " + springProfileActive);

			System.setProperty("spring.profiles.active",springProfileActive);
		} else {
			System.out.println("PROFILE_ACTIVE variable is not set.");
		}
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner loadData(InventoryRepository inventoryRepository){
	// 	return args -> {
	// 		Inventory inventory = new Inventory();
	// 		inventory.setSkuCode("iphone_13_pro_max");
	// 		inventory.setQuantity(1000);
	// 		Inventory inventory1 = new Inventory();
	// 		inventory1.setSkuCode("iphone_14_pro_max");
	// 		inventory1.setQuantity(500);
	// 		inventoryRepository.save(inventory);
	// 		inventoryRepository.save(inventory1);
	// 	};
	// }
}
