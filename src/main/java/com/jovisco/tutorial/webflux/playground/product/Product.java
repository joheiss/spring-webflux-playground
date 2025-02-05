package com.jovisco.tutorial.webflux.playground.product;

import org.springframework.data.relational.core.mapping.Table;

@Table(name = "products")
public record Product(
        Integer id,
        String description,
        Integer price
) {
}
