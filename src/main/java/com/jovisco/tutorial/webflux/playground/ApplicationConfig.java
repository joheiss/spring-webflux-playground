package com.jovisco.tutorial.webflux.playground;

import com.jovisco.tutorial.webflux.playground.product.ProductResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class ApplicationConfig {

    @Bean
    public Sinks.Many<ProductResponseDto> productSink() {
        return Sinks.many().replay().limit(1);
    }
}
