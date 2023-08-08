package com.microservice.productservice.service;

import com.microservice.productservice.dto.InventoryDto;
import com.microservice.productservice.dto.ProductRequest;
import com.microservice.productservice.dto.ProductResponse;
import com.microservice.productservice.dto.ResponseModel;
import com.microservice.productservice.model.Product;
import com.microservice.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service
@Slf4j
@Transactional
public class ProductService {

    @Value("${inventory-service-uri}")
    private String inventoryUri;
    //http://localhost:8085
//    public ProductService(ProductRepository productRepository, WebClient.Builder webClientBuilder){
//        this.productRepository = productRepository;
//        this.webClientBuilder = webClientBuilder;
//    }
    @Autowired
    private final ProductRepository productRepository;


    @Autowired
    private WebClient.Builder webClientBuilder;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ResponseModel createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        try {
            productRepository.save(product);
        } catch (Exception ex){
            ex.printStackTrace();
            return ResponseModel
                    .builder()
                    .code(999)
                    .message(ex.getMessage())
                    .build();
        }
        String skuCode = product.getName().toLowerCase().replace(" ", "_");
        InventoryDto inventoryDto = InventoryDto.builder()
                .skuCode(skuCode)
                .quantity(100)
                .build();
        Boolean inventoryFlag;
        try {
            inventoryFlag = webClientBuilder.build().post().uri(inventoryUri+"/api/inventory")
                    .body(Mono.just(inventoryDto), InventoryDto.class)
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception ex){
            ex.printStackTrace();
            log.info("Product Service Call to Inventory Service createProduct Method");
            return ResponseModel
                    .builder()
                    .code(1999)
                    .message("Inventory Service Unreachable")
                    .build();
        }
        log.info("Product {} is saved.",product.getId());
       return ResponseModel
               .builder()
               .code(1001)
               .message("CREATED")
               .data("Product ID: "+product.getId()+"! "+inventoryFlag)
               .build();
    }

    public ResponseModel getAllProducts() {
        List<Product> products;
        try {
            products=productRepository.findAll();

        } catch (Exception ex){
            ex.printStackTrace();
            return ResponseModel
                    .builder()
                    .code(999)
                    .message(ex.getMessage())
                    .build();
        }
        return ResponseModel
                .builder()
                .code(1001)
                .message("FETCH")
                .data(products.stream().map(this::mapToProductResponse).toList())
                .build();
    }
    private ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .Id(product.getId())
                .name(product.getName())
                .skuCode(product.getName().toLowerCase().replace(" ", "_"))
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
