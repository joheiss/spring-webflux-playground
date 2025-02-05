package com.jovisco.tutorial.webflux.playground.customer;

import com.jovisco.tutorial.webflux.playground.AbstractTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class CustomerRepositoryTest extends AbstractTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindAll() {

        customerRepository.findAll()
                .doOnNext(customer -> log.info("Customer: {}", customer))
                .as(StepVerifier::create)
                .expectNextCount(10)
                .expectComplete()
                .verify();
    }

    @Test
    void testFindById() {

        customerRepository.findById(7)
                .doOnNext(customer -> log.info("Customer: {}", customer))
                .as(StepVerifier::create)
                .assertNext(customer -> assertEquals("olivia", customer.getName()))
                .expectComplete()
                .verify();

        customerRepository.findById(1)
                .doOnNext(customer -> log.info("Customer: {}", customer))
                .as(StepVerifier::create)
                .assertNext(customer -> assertNotEquals("olivia", customer.getName()))
                .expectComplete()
                .verify();
    }

    @Test
    void testFindAllByName() {

        customerRepository.findByName("olivia")
                .doOnNext(customer -> log.info("Customer: {}", customer))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testFindAllByEmailEndingWith() {

        customerRepository.findByEmailEndingWith("@example.com")
                .doOnNext(customer -> log.info("Customer: {}", customer))
                .as(StepVerifier::create)
                .expectNextCount(7)
                .expectComplete()
                .verify();
    }

    @Test
    void testInsertAndDelete() {
        var customer = Customer.builder()
                .name("hansi")
                .email("hansi@horsti.de")
                .build();

        customerRepository.save(customer)
                .doOnNext(c -> log.info("Customer: {}", c))
                .as(StepVerifier::create)
                .assertNext(c -> assertNotNull(c.getId()))
                .expectComplete()
                .verify();

        customerRepository.deleteById(11)
                .then(customerRepository.count())
                .as(StepVerifier::create)
                .assertNext(count -> assertEquals(10, count))
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdate() {

        customerRepository.findByName("olivia")
                .doOnNext(c -> c.setEmail("olivia@horsti.de"))
                .flatMap(c -> customerRepository.save(c))
                .doOnNext(c -> log.info("Updated customer: {}", c))
                .as(StepVerifier::create)
                .assertNext(c -> assertNotNull(c.getId()))
                .expectComplete()
                .verify();
    }
}