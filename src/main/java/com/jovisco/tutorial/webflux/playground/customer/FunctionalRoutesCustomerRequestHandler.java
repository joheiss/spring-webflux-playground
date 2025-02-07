package com.jovisco.tutorial.webflux.playground.customer;

import com.jovisco.tutorial.webflux.playground.shared.ApplicationExceptionsFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class FunctionalRoutesCustomerRequestHandler {

    private final CustomerService customerService;

    public Mono<ServerResponse> getCustomers(ServerRequest request) {
        log.info("FUNCTIONAL ROUTES ...");
        return customerService.getAllCustomers()
                .as(flux -> ServerResponse.ok().body(flux, CustomerResponseDto.class));
    }

    public Mono<ServerResponse> getCustomersPaginated(ServerRequest request) {
        var pageNo = request.queryParam("pageNo").map(Integer::valueOf).orElse(1);
        var pageSize = request.queryParam("pageSize").map(Integer::valueOf).orElse(5);
        return customerService.getAllCustomers(pageNo, pageSize)
                .as(flux -> ServerResponse.ok().body(flux, CustomerResponseDto.class));
    }

    public Mono<ServerResponse> getCustomer(ServerRequest request) {
        var id = Integer.valueOf(request.pathVariable("id"));
        return customerService.getCustomerById(id)
                .switchIfEmpty(ApplicationExceptionsFactory.customerNotFound(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> createCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerRequestDto.class)
                .transform(CustomerRequestValidator.validate())
                .as(customerService::createCustomer)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request) {
        var id = Integer.valueOf(request.pathVariable("id"));
        return request.bodyToMono(CustomerRequestDto.class)
                .transform(CustomerRequestValidator.validate())
                .as(validated -> customerService.updateCustomer(id, validated))
                .switchIfEmpty(ApplicationExceptionsFactory.customerNotFound(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
        var id = Integer.valueOf(request.pathVariable("id"));
        return customerService.deleteCustomer(id)
                .filter(found -> found)
                .switchIfEmpty(ApplicationExceptionsFactory.customerNotFound(id))
                .then(ServerResponse.ok().build());
    }
}
