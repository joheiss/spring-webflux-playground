package com.jovisco.tutorial.webflux.playground.product;

public record ProductRequestDto(
        // Integer id,
        String description,
        Integer price
) {
}
