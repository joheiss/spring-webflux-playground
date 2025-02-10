package com.jovisco.tutorial.webflux.playground;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

@Slf4j
abstract class AbstractWebClient {

    protected WebClient createWebClient() {
        return createWebClient(builder -> {});
    }

    protected WebClient createWebClient(Consumer<WebClient.Builder> consumer) {
        var builder = WebClient.builder()
                .baseUrl("http://localhost:7070");
        consumer.accept(builder);
        return builder.build();
    }
}
