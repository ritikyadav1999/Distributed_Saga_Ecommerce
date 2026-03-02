package org.example.orderservice.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(

        UUID eventId,
        String eventType,
        UUID userId,
        UUID orderId,
        UUID orderItemId,
        Integer quantity,
        BigDecimal price,
        String correlationId,
        String eventVersion

) {
}
