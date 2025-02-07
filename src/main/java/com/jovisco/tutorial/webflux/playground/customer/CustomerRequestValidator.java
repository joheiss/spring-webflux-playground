package com.jovisco.tutorial.webflux.playground.customer;

import com.jovisco.tutorial.webflux.playground.shared.ApplicationExceptionsFactory;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class CustomerRequestValidator {

    public static UnaryOperator<Mono<CustomerRequestDto>> validate() {
        return mono -> mono.filter(hasName())
                .switchIfEmpty(ApplicationExceptionsFactory.missingName())
                .filter(hasValidEmail())
                .switchIfEmpty(ApplicationExceptionsFactory.missingValidEmail());
    }

    private static Predicate<CustomerRequestDto> hasName() {
        return dto -> Objects.nonNull(dto.name());
    }

    private static Predicate<CustomerRequestDto> hasValidEmail() {
        return dto -> Objects.nonNull(dto.email()) && dto.email().contains("@");
    }
}
