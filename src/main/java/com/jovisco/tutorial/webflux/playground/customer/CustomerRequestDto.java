package com.jovisco.tutorial.webflux.playground.customer;

public record CustomerRequestDto(
    String name,
    String email
) {
}
