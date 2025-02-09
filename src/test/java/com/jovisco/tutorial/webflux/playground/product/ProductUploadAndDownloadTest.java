package com.jovisco.tutorial.webflux.playground.product;

import com.jovisco.tutorial.webflux.playground.shared.util.FileWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.time.Duration;

@Slf4j
public class ProductUploadAndDownloadTest {

    private final ProductClient productClient = new ProductClient();

    @Test
    public void testUploadOneProduct() {
        var load = Flux
                .just(new ProductRequestDto("Wirbelwind", 123))
                .delayElements(Duration.ofSeconds(3L));

        productClient.uploadProducts(load)
                .doOnNext(p -> log.info("Loaded: {}", p))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void testUploadManyProducts() {
        var load = Flux.range(1, 1_000_000)
                .map(i -> new ProductRequestDto("Product-M" + i, i));

        productClient.uploadProducts(load)
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void testDownloadProducts() {
        productClient.downloadProducts()
                .map(ProductResponseDto::toString)
                .as(flux -> FileWriter.create(flux, Path.of("products.txt")))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
