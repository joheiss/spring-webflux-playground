package com.jovisco.tutorial.webflux.playground.product;

public class ProductMapper {

    public static Product toEntity(ProductRequestDto productRequestDto) {
        return Product.builder()
                .description(productRequestDto.description())
                .price(productRequestDto.price())
                .build();
    }

    public static ProductResponseDto toResponseDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
