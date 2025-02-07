package com.jovisco.tutorial.webflux.playground.customer;

public class CustomerMapper {

    public static Customer toEntity(CustomerRequestDto dto) {
        return Customer.builder()
                .name(dto.name())
                .email(dto.email())
                .build();
    }

    public static CustomerResponseDto toResponseDto(Customer entity) {
        return new CustomerResponseDto(entity.getId(), entity.getName(), entity.getEmail());
    }
}
