package com.microservice.inventoryservice.controller;

import com.microservice.inventoryservice.dto.InventoryDto;
import com.microservice.inventoryservice.dto.InventoryResponse;
import com.microservice.inventoryservice.dto.ResponseModel;
import com.microservice.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // http://localhost:8082/api/inventory/iphone-13-pro-max,iphone-14-pro-max
    // http://localhost:8082/api/inventory?skuCode=iphone-13-pro-max?skuCode=iphone-14-pro-max
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public String orderAddedSucessfully(@RequestBody Map<String,String> orderAndQuantity){
        inventoryService.orderAddedSucessfully(orderAndQuantity);
        return "Order Added Successfully";
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<InventoryResponse> isInStockAndQuantity(@RequestBody Map<String,String> orderAndQuantity){
        return inventoryService.isInStock(orderAndQuantity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean createProductInventory(@RequestBody InventoryDto inventoryDto){
        return inventoryService.createProductInventory(inventoryDto);
    }
}
