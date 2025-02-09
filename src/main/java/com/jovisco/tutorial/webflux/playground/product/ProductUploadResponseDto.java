package com.jovisco.tutorial.webflux.playground.product;

import java.util.UUID;

public record ProductUploadResponseDto(
        UUID correlationId,
        Long count
) {
}
