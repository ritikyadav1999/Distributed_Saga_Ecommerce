package org.example.paymentservice.event;

import java.math.BigDecimal;
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
