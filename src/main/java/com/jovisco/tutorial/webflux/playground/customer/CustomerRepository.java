package com.jovisco.tutorial.webflux.playground.customer;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    Flux<Customer> findByName(String name);
    Flux<Customer> findByEmailEndingWith(String emailEnding);
    Flux<Customer> findAllBy(Pageable pageable);
    @Modifying
    @Query("DELETE FROM customers WHERE id = :id")
    Mono<Boolean> deleteCustomerById(Integer id);
}
