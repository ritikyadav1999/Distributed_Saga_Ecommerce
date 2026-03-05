package org.example.paymentservice.event;

import java.util.UUID;

public record InventoryEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        UUID orderId,
        String correlationId,
        String eventVersion
) {
}