package com.jovisco.tutorial.webflux.playground.customer;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "customers")
public class Customer {
    @Id
    private Integer id;
    private String name;
    private String email;
}
