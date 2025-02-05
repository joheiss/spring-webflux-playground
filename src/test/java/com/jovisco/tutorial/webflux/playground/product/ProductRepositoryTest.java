package com.jovisco.tutorial.webflux.playground.product;

import com.jovisco.tutorial.webflux.playground.AbstractTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ProductRepositoryTest extends AbstractTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindByPriceBetween() {

        productRepository.findByPriceBetween(250, 1000)
                .doOnNext(p -> log.info("Product found: {}", p))
                .as(StepVerifier::create)
                .assertNext(p -> assertEquals(1000, p.price()))
                .expectNextCount(5)
                .expectComplete()
                .verify();
    }

    @Test
    void testFindAllByPageable() {

        productRepository.findAllBy(PageRequest.of(0, 5).withSort(Sort.Direction.ASC, "price"))
                .doOnNext(p -> log.info("Product found: {}", p))
                .as(StepVerifier::create)
                .assertNext(p -> assertEquals(200, p.price()))
                .expectNextCount(4)
                .expectComplete()
                .verify();
    }

}