package com.jovisco.tutorial.webflux.playground.shared.functionalRoutes.config;

import com.jovisco.tutorial.webflux.playground.customer.CustomerNotFoundException;
import com.jovisco.tutorial.webflux.playground.customer.FunctionalRoutesCustomerRequestHandler;
import com.jovisco.tutorial.webflux.playground.shared.InvalidInputException;
import com.jovisco.tutorial.webflux.playground.shared.functionalRoutes.FunctionalRoutesExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FunctionalRoutesConfig {

    private final FunctionalRoutesCustomerRequestHandler customerRequestHandler;
    private final FunctionalRoutesExceptionHandler exceptionHandler;

//    @RouterOperations({
//            @RouterOperation(path = "/customers",
//                    beanClass = FunctionalRoutesCustomerRequestHandler.class,
//                    beanMethod = "getCustomers"),
//            @RouterOperation(path = "/customers/paginated",
//                    beanClass = FunctionalRoutesCustomerRequestHandler.class,
//                    beanMethod = "getCustomersPaginated"),
//            @RouterOperation(path = "/customers/{id}",
//                    beanClass = FunctionalRoutesCustomerRequestHandler.class,
//                    beanMethod = "getCustomer"),
//            @RouterOperation(path = "/customers", method = RequestMethod.POST,
//                    beanClass = FunctionalRoutesCustomerRequestHandler.class,
//                    beanMethod = "createCustomer"),
//            @RouterOperation(path = "/customers/{id}", method = RequestMethod.PUT,
//                    beanClass = FunctionalRoutesCustomerRequestHandler.class,
//                    beanMethod = "updateCustomer"),
//            @RouterOperation(path = "/customers/{id}", method = RequestMethod.DELETE,
//                    beanClass = FunctionalRoutesCustomerRequestHandler.class,
//                    beanMethod = "deleteCustomer")
//    })
//    @Bean
    public RouterFunction<ServerResponse> customerRoutes() {
        return RouterFunctions.route()
                .filter((request, next) -> {
                    log.info("Request path: {}", request.path());
                    if (request.path().contains("swagger") || request.path().contains("api-docs")) {
                        log.info("Request path: {}", request.path());
                        request.attributes().put("userCategory", "STANDARD");
                    }
                    return next.handle(request);
                })
                .GET("/customers", this.customerRequestHandler::getCustomers)
                .GET("/customers/paginated", this.customerRequestHandler::getCustomersPaginated)
                .GET("/customers/{id}", this.customerRequestHandler::getCustomer)
                .POST("/customers", this.customerRequestHandler::createCustomer)
                .PUT("/customers/{id}", this.customerRequestHandler::updateCustomer)
                .DELETE("/customers/{id}", this.customerRequestHandler::deleteCustomer)
                .onError(CustomerNotFoundException.class, this.exceptionHandler::handleException)
                .onError(InvalidInputException.class, this.exceptionHandler::handleException)
                .build();
    }
}
