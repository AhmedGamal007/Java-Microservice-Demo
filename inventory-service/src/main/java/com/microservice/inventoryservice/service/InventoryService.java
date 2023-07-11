package com.microservice.inventoryservice.service;

import com.microservice.inventoryservice.dto.InventoryDto;
import com.microservice.inventoryservice.dto.InventoryResponse;
import com.microservice.inventoryservice.dto.ResponseModel;
import com.microservice.inventoryservice.mapper.InventoryMapper;
import com.microservice.inventoryservice.model.Inventory;
import com.microservice.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public void orderAddedSucessfully(Map<String,String> orderAndQuantity){
        List<String> skuCode = orderAndQuantity.keySet().stream().toList();
            inventoryRepository.findBySkuCodeIn(skuCode).stream().forEach(inventory -> {
                inventory.setQuantity(inventory.getQuantity()-Integer.parseInt(orderAndQuantity.get(inventory.getSkuCode())));
                inventoryRepository.save(inventory);
            });

    }

    public Boolean createProductInventory(InventoryDto inventoryDto){
        try {
        Inventory inventory;
        inventory = inventoryRepository.save(InventoryMapper.inventoryDtoToInventory(inventoryDto));
        log.info("Product Inventory Created ID: "+inventory.getId()+"!");
        return true;
        } catch (Exception e) {
         e.printStackTrace();
         return false;
        }
    }
    public List<InventoryResponse> isInStock(Map<String,String> orderAndQuantity){
        List<String> skuCode = orderAndQuantity.keySet().stream().toList();
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() >= Integer.parseInt(orderAndQuantity.get(inventory.getSkuCode())))
                        .build()).toList();
    }
}
