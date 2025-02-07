package com.jovisco.tutorial.webflux.playground.customer;

public record CustomerResponseDto(
    Integer id,
    String name,
    String email
) {
}
