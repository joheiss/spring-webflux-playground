package com.jovisco.tutorial.webflux.playground.product.traditional;

import com.jovisco.tutorial.webflux.playground.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("traditional")
public class TraditionalProductController {

    private final RestClient restClient = RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory())
            .baseUrl("http://localhost:7070")
            .build();

    @GetMapping("products")
    public List<Product> getProducts() {
        var list =  restClient.get()
                .uri("/demo01/products")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Product>>() {});
        log.info("Received response:   {}", list);
        return list;
    }
}
