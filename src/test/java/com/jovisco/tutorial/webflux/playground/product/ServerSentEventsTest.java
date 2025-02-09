package com.jovisco.tutorial.webflux.playground.product;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@AutoConfigureWebTestClient
@SpringBootTest
public class ServerSentEventsTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testServerSentEvents() {
        this.webTestClient.get()
                .uri("/products/stream/{maxPrice}", 100)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductResponseDto.class)
                .getResponseBody()
                .take(3)
                .doOnNext(p -> log.info("{}", p))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(list -> {
                    assertEquals(3, list.size());
                    assertTrue(list.stream().allMatch(p -> p.price() <= 100));
                })
                .expectComplete()
                .verify();
    }
}
