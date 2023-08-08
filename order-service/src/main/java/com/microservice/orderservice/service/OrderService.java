package com.microservice.orderservice.service;

import com.microservice.orderservice.dto.InventoryResponse;
import com.microservice.orderservice.dto.OrderLineItemsDto;
import com.microservice.orderservice.dto.OrderRequest;
import com.microservice.orderservice.dto.ResponseModel;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.model.OrderLineItems;
import com.microservice.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    @Value("${inventory-service-uri}")
    private String inventoryUri;
    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;


    public ResponseModel placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);
        Map<String, String> orderAndQuantity = new HashMap<>();

        log.warn(inventoryUri);

        order.getOrderLineItemsList().stream().map(OrderLineItems->orderAndQuantity.put(OrderLineItems.getSkuCode(),OrderLineItems.getQuantityString())).toList();
        System.out.println(order.getOrderLineItemsList());
        System.out.println(orderAndQuantity);
        //Call Inventory Service and place order if product is in stock
        InventoryResponse[] inventoryResponseArray;
        try {
            inventoryResponseArray = webClientBuilder.build().put().uri(inventoryUri+"/api/inventory")
                    .body(Mono.just(orderAndQuantity),Map.class)
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
        } catch (Exception ex){
            ex.printStackTrace();
            log.info("Order Service PUT Call to Inventory Service placeOrder Method!");
            return ResponseModel
                    .builder()
                    .code(1999)
                    .message(ex.getMessage())
                    .build();

        }
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse -> inventoryResponse.getIsInStock());
        if(allProductsInStock) {
            try {
                orderRepository.save(order);
            } catch (Exception ex){
                ex.printStackTrace();
                log.info("Saving order in order service in place order method! (DB Error)");
                return ResponseModel
                        .builder()
                        .code(999)
                        .message(ex.getMessage())
                        .build();
            }
            try {
                return ResponseModel
                        .builder()
                        .code(901)
                        .message("CREATED")
                        .data(
                                webClientBuilder.build().patch().uri(inventoryUri+"/api/inventory")
                                        .body(Mono.just(orderAndQuantity), Map.class)
                                        .header(CONTENT_TYPE, APPLICATION_JSON)
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .block()
                        )
                        .build();
            } catch (Exception ex){
                ex.printStackTrace();
                log.info("Order Service PATCH Call to Inventory Service placeOrder Method!");
                return ResponseModel
                        .builder()
                        .code(1999)
                        .message(ex.getMessage())
                        .build();
            }

            // TNS-??? Listener Exception
            // ORA-??? Database Engine Exception Internal External Implicit Explicit
        }
        else{
            return ResponseModel
                    .builder()
                    .code(899)
                    .message("OUTAGE")
                    .data("Product is not in stock, Please try again later.")
                    .build();
        }
    }
    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems= new OrderLineItems();
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
