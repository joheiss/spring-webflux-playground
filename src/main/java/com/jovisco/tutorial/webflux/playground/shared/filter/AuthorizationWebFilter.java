package com.jovisco.tutorial.webflux.playground.shared.filter;

import com.jovisco.tutorial.webflux.playground.shared.UserCategory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

@Order(20)
@Service
public class AuthorizationWebFilter implements WebFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var userCategory = exchange.getAttributeOrDefault("userCategory", UserCategory.STANDARD);
        return switch(userCategory) {
            case STANDARD -> handleStandardUsers(exchange, chain);
            case PREMIUM -> handlePremiumUsers(exchange, chain);
        };
     }

    private Mono<Void> handlePremiumUsers(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange);
    }

    private Mono<Void> handleStandardUsers(ServerWebExchange exchange, WebFilterChain chain) {
        var isGetRequest = HttpMethod.GET.equals(exchange.getRequest().getMethod());
        if (isGetRequest) {
            return chain.filter(exchange);
        }
        return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
    }
}
