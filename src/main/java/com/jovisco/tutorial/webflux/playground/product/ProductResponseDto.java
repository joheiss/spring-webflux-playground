package com.jovisco.tutorial.webflux.playground.product;

public record ProductResponseDto(
        Integer id,
        String description,
        Integer price
) {
}
