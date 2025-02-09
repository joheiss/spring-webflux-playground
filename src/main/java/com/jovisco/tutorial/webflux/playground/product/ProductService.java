package com.jovisco.tutorial.webflux.playground.product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<ProductResponseDto> getProducts();
    Flux<ProductResponseDto> loadProducts(Flux<ProductRequestDto> load);
    Mono<Long> countProducts();
    Mono<ProductResponseDto> createProduct(Mono<ProductRequestDto> productRequestDto);
    Flux<ProductResponseDto> getProductStream();
}
