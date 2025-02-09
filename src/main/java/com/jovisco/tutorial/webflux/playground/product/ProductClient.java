package com.jovisco.tutorial.webflux.playground.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ProductClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader("auth-token", "secret456")
            .build();

   public Mono<ProductUploadResponseDto> uploadProducts(Flux<ProductRequestDto> load) {
       return webClient.post()
               .uri("/products/upload")
               .contentType(MediaType.APPLICATION_NDJSON)
               .body(load, ProductRequestDto.class)
               .retrieve()
               .bodyToMono(ProductUploadResponseDto.class);
   }

   public Flux<ProductResponseDto> downloadProducts() {
       return webClient.get()
               .uri("/products/download")
               .accept(MediaType.APPLICATION_NDJSON)
               .retrieve()
               .bodyToFlux(ProductResponseDto.class);
   }
}
