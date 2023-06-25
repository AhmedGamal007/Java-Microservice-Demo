package com.microservice.orderservice.service;

import com.microservice.orderservice.dto.InventoryResponse;
import com.microservice.orderservice.dto.OrderLineItemsDto;
import com.microservice.orderservice.dto.OrderRequest;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.model.OrderLineItems;
import com.microservice.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    public ResponseEntity<String> placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);
        Map<String, String> orderAndQuantity = new HashMap<>();

        order.getOrderLineItemsList().stream().map(OrderLineItems->orderAndQuantity.put(OrderLineItems.getSkuCode(),OrderLineItems.getQuantityString())).toList();
        System.out.println(order.getOrderLineItemsList());
        System.out.println(orderAndQuantity);
        //Call Inventory Service and place order if product is in stock
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().post().uri("http://localhost:8085/api/inventory")
                .body(Mono.just(orderAndQuantity),Map.class)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse -> inventoryResponse.getIsInStock());
        if(allProductsInStock) {
            orderRepository.save(order);
            return ResponseEntity.ok().body(
                    webClientBuilder.build().patch().uri("http://localhost:8085/api/inventory")
                            .body(Mono.just(orderAndQuantity),Map.class)
                            .header(CONTENT_TYPE, APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block());

        }
        else{
            return ResponseEntity.badRequest().body("Product is not in stock, Please try again later.");
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
