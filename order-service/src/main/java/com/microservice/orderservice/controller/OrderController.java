package com.microservice.orderservice.controller;

import com.microservice.orderservice.dto.OrderRequest;
import com.microservice.orderservice.dto.ResponseModel;
import com.microservice.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public ResponseModel placeOrder(@RequestBody OrderRequest orderRequest){
        log.info("place Order Controller");
        return orderService.placeOrder(orderRequest);
    }
}
