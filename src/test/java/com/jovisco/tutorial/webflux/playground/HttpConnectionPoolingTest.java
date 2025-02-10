package com.jovisco.tutorial.webflux.playground;

import com.jovisco.tutorial.webflux.playground.product.ProductResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpConnectionPoolingTest extends AbstractWebClient {

    /*
        It is for demo purposes! You might NOT need to adjust all these!
        If the response time 100ms => 500 / (100 ms) ==> 5000 req / sec
     */
    private final WebClient client = createWebClient(b -> {
        var poolSize = 1000;
        var provider = ConnectionProvider.builder("connection-pool")
                .lifo()
                .maxConnections(poolSize)
                .pendingAcquireMaxCount(poolSize * 5)
                .build();
        var httpClient = HttpClient.create(provider)
                .compress(true)
                .keepAlive(true);
        b.clientConnector(new ReactorClientHttpConnector(httpClient));
    });

    @Test
    public void testConcurrentRequests() {
        var max = 1000;
        Flux.range(1, max)
                .flatMap(this::getProduct, max)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(list -> assertEquals(max, list.size()))
                .expectComplete()
                .verify();
    }

    private Mono<ProductResponseDto> getProduct(int id) {
        return this.client.get()
                .uri("demo03/product/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponseDto.class);
    }
}
