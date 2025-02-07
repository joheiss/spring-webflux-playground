package com.jovisco.tutorial.webflux.playground.customer;

public class CustomerNotFoundException extends RuntimeException {

  public static final String MESSAGE = "Customer [id=%d]not found";

    public CustomerNotFoundException(Integer id) {
        super(MESSAGE.formatted(id));
    }
}
