package org.example.orderservice.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull
        UUID userId,
        @NotNull
        UUID orderItemId,

        @NotNull
        Integer quantity,

        @NotNull
        BigDecimal price
) {
}
