package com.jovisco.tutorial.webflux.playground.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = "download", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ProductResponseDto> downloadProducts() {
        log.info("Download invoked");
        return productService.getProducts();
    }

    @PostMapping(value = "upload", consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<ProductUploadResponseDto> uploadProducts(
            @RequestBody Flux<ProductRequestDto> load
    ) {
        log.info("Upload invoked");
        return productService
                .loadProducts(load)
                .then(productService.countProducts())
                .map(count -> new ProductUploadResponseDto(UUID.randomUUID(), count));
    }

    @PostMapping
    public Mono<ProductResponseDto> createProduct(@RequestBody Mono<ProductRequestDto> productRequestDto) {
        return productRequestDto
                .transform(ProductRequestValidator.validate())
                .as(productService::createProduct);
    }

    @GetMapping(value = "stream/{maxPrice}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductResponseDto> getProductStream(@PathVariable Integer maxPrice) {
        return productService.getProductStream()
                .filter(p -> p.price() <= maxPrice);
    }
}
