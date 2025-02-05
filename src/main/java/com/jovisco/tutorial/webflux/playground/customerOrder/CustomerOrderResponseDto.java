package com.jovisco.tutorial.webflux.playground.customerOrder;

import java.time.Instant;
import java.util.UUID;

public record CustomerOrderResponseDto(
        UUID orderId,
        String customerName,
        String productName,
        Integer amount,
        Instant orderDate
) {
}
