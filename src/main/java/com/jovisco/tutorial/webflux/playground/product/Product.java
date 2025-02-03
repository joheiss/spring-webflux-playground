package com.jovisco.tutorial.webflux.playground.product;

public record Product(
        Integer id,
        String description,
        Integer price
) {
}
