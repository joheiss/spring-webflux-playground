package com.jovisco.tutorial.webflux.playground.shared;

import com.jovisco.tutorial.webflux.playground.customer.CustomerNotFoundException;
import reactor.core.publisher.Mono;

public class ApplicationExceptionsFactory {

    public static <T> Mono<T> customerNotFound(Integer id) {
        return Mono.error(new CustomerNotFoundException(id));
    }

    public static <T> Mono<T> missingName() {
        return Mono.error(new InvalidInputException("Name is required"));
    }

    public static <T> Mono<T> missingValidEmail() {
        return Mono.error(new InvalidInputException("Valid email is required"));
    }
}
