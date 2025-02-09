package com.jovisco.tutorial.webflux.playground.product;

import com.jovisco.tutorial.webflux.playground.customer.CustomerRequestDto;
import com.jovisco.tutorial.webflux.playground.shared.ApplicationExceptionsFactory;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ProductRequestValidator {

    public static UnaryOperator<Mono<ProductRequestDto>> validate() {
        return mono -> mono.filter(hasDescription())
                .switchIfEmpty(ApplicationExceptionsFactory.missingDescription())
                .filter(hasPrice())
                .switchIfEmpty(ApplicationExceptionsFactory.missingPrice());
    }

    private static Predicate<ProductRequestDto> hasDescription() {
        return dto -> Objects.nonNull(dto.description());
    }

    private static Predicate<ProductRequestDto> hasPrice() {
        return dto -> Objects.nonNull(dto.price());
    }
}
