package com.jovisco.tutorial.webflux.playground.product;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "products")
public class Product {
    @Id
    private Integer id;
    private String description;
    private Integer price;
}

