package com.jovisco.tutorial.webflux.playground.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Integer> {

    Flux<Product> findByPriceBetween(Integer from, Integer to);

    Flux<Product> findAllBy(Pageable pageable);
}
