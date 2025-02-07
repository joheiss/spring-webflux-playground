package com.jovisco.tutorial.webflux.playground.customer;

import com.jovisco.tutorial.webflux.playground.shared.ApplicationExceptionsFactory;
import com.jovisco.tutorial.webflux.playground.shared.UserCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequestMapping("customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public Flux<CustomerResponseDto> getCustomers(
            @RequestHeader("auth-token") String token,
            @RequestAttribute("userCategory") UserCategory userCategory
    ) {
        log.info("User category: {}", userCategory);
        return customerService.getAllCustomers();
    }

    @GetMapping("paginated")
    public Flux<CustomerResponseDto> getCustomers(
            @RequestHeader("auth-token") String token,
            @RequestParam(defaultValue = "1" ) Integer pageNo,
            @RequestParam(defaultValue = "5") Integer pageSize
    ) {
        return customerService.getAllCustomers(pageNo, pageSize);
    }

    /*
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @GetMapping("{id}")
    public Mono<ResponseEntity<CustomerResponseDto>> getCustomerResponseDto(@PathVariable Integer id) {

        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    */
    @GetMapping("{id}")
    public Mono<CustomerResponseDto> getCustomerResponseDto(
            @RequestHeader("auth-token") String token,
            @PathVariable Integer id
    ) {

        return customerService.getCustomerById(id)
                .switchIfEmpty(ApplicationExceptionsFactory.customerNotFound(id));
    }

    /*
    @PostMapping
    public Mono<CustomerResponseDto> createCustomer(@RequestBody Mono<CustomerRequestDto> mono) {
        return customerService.createCustomer(mono);
    }
    */

    @PostMapping
    public Mono<CustomerResponseDto> createCustomer(
            @RequestHeader("auth-token") String token,
            @RequestBody Mono<CustomerRequestDto> mono
    ) {
        return mono.transform(CustomerRequestValidator.validate())
                .as(this.customerService::createCustomer);
    }

    /*
    @PutMapping("{id}")
    public Mono<ResponseEntity<CustomerResponseDto>> updateCustomer(
            @PathVariable Integer id,
            @RequestBody Mono<CustomerRequestDto> mono) {

        return customerService.updateCustomer(id, mono)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    */

    @PutMapping("{id}")
    public Mono<CustomerResponseDto> updateCustomer(
            @RequestHeader("auth-token") String token,
            @PathVariable Integer id,
            @RequestBody Mono<CustomerRequestDto> mono
    ) {

        return mono.transform(CustomerRequestValidator.validate())
                .as(validReq -> this.customerService.updateCustomer(id, validReq))
                .switchIfEmpty(ApplicationExceptionsFactory.customerNotFound(id));
    }

    /*
    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable Integer id) {
        return customerService.deleteCustomer(id)
                .filter(found -> found)
                .map(found -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    */

    @DeleteMapping("{id}")
    public Mono<Void> deleteCustomer(
            @RequestHeader("auth-token") String token,
            @PathVariable Integer id
    ) {
        return customerService.deleteCustomer(id)
                .filter(found -> found)
                .switchIfEmpty(ApplicationExceptionsFactory.customerNotFound(id))
                .then();
    }
}
