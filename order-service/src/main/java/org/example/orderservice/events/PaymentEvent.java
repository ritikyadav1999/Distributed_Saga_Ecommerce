package org.example.orderservice.events;

import java.util.UUID;

public record PaymentEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        UUID orderId,
        String correlationId,
        String eventVersion

) {
}
