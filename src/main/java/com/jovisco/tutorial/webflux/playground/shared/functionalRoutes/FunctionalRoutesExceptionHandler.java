package com.jovisco.tutorial.webflux.playground.shared.functionalRoutes;

import com.jovisco.tutorial.webflux.playground.customer.CustomerNotFoundException;
import com.jovisco.tutorial.webflux.playground.shared.InvalidInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

@Service
public class FunctionalRoutesExceptionHandler {

    public Mono<ServerResponse> handleException(CustomerNotFoundException ex, ServerRequest request) {
        return handleException(HttpStatus.NOT_FOUND, ex, request, problem -> {
            problem.setType(URI.create("http://example.com/problems/customers/customer-not-found"));
            problem.setTitle("Customer Not Found");
        });
    }

    public Mono<ServerResponse> handleException(InvalidInputException ex, ServerRequest request) {
        return handleException(HttpStatus.BAD_REQUEST, ex, request, problem -> {
            problem.setType(URI.create("http://example.com/problems/customers/invalid-input"));
            problem.setTitle("Invalid Input");
        });
    }
    private Mono<ServerResponse> handleException(HttpStatus status, Exception ex, ServerRequest request, Consumer<ProblemDetail> consumer) {
        var problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setInstance(URI.create(request.path()));
        consumer.accept(problem);
        return ServerResponse.status(status).bodyValue(problem);
    }
}
