package org.example.inventoryservice.event;

import java.math.BigDecimal;
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
