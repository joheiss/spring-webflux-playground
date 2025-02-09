package com.jovisco.tutorial.webflux.playground.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final Sinks.Many<ProductResponseDto> productSink;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, Sinks.Many<ProductResponseDto> productSink) {
        this.productRepository = productRepository;
        this.productSink = productSink;
    }

    @Override
    public Flux<ProductResponseDto> getProducts() {
        return productRepository.findAll()
                .map(ProductMapper::toResponseDto);
    }

    @Override
    public Flux<ProductResponseDto> loadProducts(Flux<ProductRequestDto> load) {
       return load.map(ProductMapper::toEntity)
               .as(productRepository::saveAll)
               .map(ProductMapper::toResponseDto);
    }

    @Override
    public Mono<Long> countProducts() {
        return productRepository.count();
    }

    @Override
    public Mono<ProductResponseDto> createProduct(Mono<ProductRequestDto> productRequestDto) {
        return productRequestDto
                .map(ProductMapper::toEntity)
                .flatMap(productRepository::save)
                .map(ProductMapper::toResponseDto)
                .doOnNext(productSink::tryEmitNext);
    }

    @Override
    public Flux<ProductResponseDto> getProductStream() {
        return productSink.asFlux();
    }
}
