package com.microservice.inventoryservice.mapper;

import com.microservice.inventoryservice.model.Inventory;
import com.microservice.inventoryservice.dto.InventoryDto;

public class InventoryMapper {
    
    public static Inventory inventoryDtoToInventory(InventoryDto inventoryDto){

        return Inventory.builder()
        .skuCode(inventoryDto.getSkuCode())
        .quantity(inventoryDto.getQuantity())
        .build();
    }
}
