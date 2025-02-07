package com.jovisco.tutorial.webflux.playground.shared.filter;

import com.jovisco.tutorial.webflux.playground.shared.UserCategory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

@Order(10)
@Service
public class AuthenticationWebFilter implements WebFilter {

    public static final Map<String, UserCategory> USER_CATEGORIES = Map.of(
            "secret123", UserCategory.STANDARD,
            "secret456", UserCategory.PREMIUM
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var path = exchange.getRequest().getURI().getPath();
        if (path.contains("swagger") || path.contains("api-docs")) {
            exchange.getAttributes().put("userCategory", UserCategory.STANDARD);
            return chain.filter(exchange);
        }
        var token = exchange.getRequest().getHeaders().getFirst("auth-token");
        if (null != token && USER_CATEGORIES.containsKey(token)) {
            exchange.getAttributes().put("userCategory", USER_CATEGORIES.get(token));
            return chain.filter(exchange);
        }
        return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
    }
}
