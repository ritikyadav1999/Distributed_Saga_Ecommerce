package org.example.inventoryservice.event;

import java.math.BigDecimal;
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
