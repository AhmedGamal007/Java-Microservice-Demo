package com.microservice.productservice.controller;

import com.microservice.productservice.dto.ProductRequest;
import com.microservice.productservice.dto.ProductResponse;
import com.microservice.productservice.dto.ResponseModel;
import com.microservice.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @CrossOrigin
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseModel createProduct(@RequestBody ProductRequest productRequest){
        return productService.createProduct(productRequest);
    }
    @GetMapping
    @CrossOrigin
    @ResponseStatus(HttpStatus.OK)
    public ResponseModel getAllProducts(){
       return productService.getAllProducts();
    }
}
