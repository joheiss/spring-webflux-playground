package com.jovisco.tutorial.webflux.playground.customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerResponseDto> getAllCustomers();
    Flux<CustomerResponseDto> getAllCustomers(Integer pageNo, Integer pageSize);

    Mono<CustomerResponseDto> getCustomerById(Integer id);

    Mono<CustomerResponseDto> createCustomer(Mono<CustomerRequestDto> mono);

    Mono<CustomerResponseDto> updateCustomer(Integer id, Mono<CustomerRequestDto> mono);

    Mono<Boolean> deleteCustomer(Integer id);
}
