package com.microservice.inventoryservice.service;

import com.microservice.inventoryservice.dto.InventoryResponse;
import com.microservice.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public void orderAddedSucessfully(Map<String,String> orderAndQuantity){
        List<String> skuCode = orderAndQuantity.keySet().stream().toList();
        System.out.println("OrderAdded INVENTORYYYYY");
        inventoryRepository.findBySkuCodeIn(skuCode).stream().forEach(inventory -> {
            System.out.println("OrderADED WORKINGG");
            inventory.setQuantity(inventory.getQuantity()-Integer.parseInt(orderAndQuantity.get(inventory.getSkuCode())));
            inventoryRepository.save(inventory);
        });


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
