package com.jovisco.tutorial.webflux.playground.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Flux<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll()
                .map(CustomerMapper::toResponseDto);
    }

    @Override
    public Flux<CustomerResponseDto> getAllCustomers(Integer pageNo, Integer pageSize) {
        return customerRepository.findAllBy(PageRequest.of(pageNo - 1, pageSize))
                .map(CustomerMapper::toResponseDto);
    }

    @Override
    public Mono<CustomerResponseDto> getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .map(CustomerMapper::toResponseDto);
    }

    @Override
    public Mono<CustomerResponseDto> createCustomer(Mono<CustomerRequestDto> mono) {
        return mono
                .map(CustomerMapper::toEntity)
                .flatMap(customerRepository::save)
                .map(CustomerMapper::toResponseDto);
    }

    @Override
    public Mono<CustomerResponseDto> updateCustomer(Integer id, Mono<CustomerRequestDto> mono) {
        return customerRepository.findById(id)
                .flatMap(found -> mono)
                .map(CustomerMapper::toEntity)
                .doOnNext(c -> c.setId(id))
                .flatMap(customerRepository::save)
                .map(CustomerMapper::toResponseDto);
    }

    @Override
    public Mono<Boolean> deleteCustomer(Integer id) {
        return customerRepository.deleteCustomerById(id);
    }
}
