package com.jovisco.tutorial.webflux.playground.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ProductLoader implements CommandLineRunner {

    private final ProductService productService;

    @Autowired
    public ProductLoader(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        Flux.range(1, 1000)
                .delayElements(Duration.ofSeconds(1L))
                .map( i -> new ProductRequestDto("Product-" + i, i))
                .flatMap(dto -> productService.createProduct(Mono.just(dto)))
                .subscribe();
    }
}
