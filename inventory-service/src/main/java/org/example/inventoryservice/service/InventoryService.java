package org.example.inventoryservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inventoryservice.entity.Outbox;
import org.example.inventoryservice.entity.OutboxStatus;
import org.example.inventoryservice.entity.ProcessedEvent;
import org.example.inventoryservice.event.InventoryEvent;
import org.example.inventoryservice.event.OrderCreatedEvent;
import org.example.inventoryservice.repository.OutboxRepo;
import org.example.inventoryservice.repository.ProcessedEventRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InventoryService {

    private final OutboxRepo outboxRepo;
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepo processedEventRepo;


    @Transactional
    public void processOrder(OrderCreatedEvent event) {

        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setEventId(event.eventId());
        processedEvent.setCorrelationId(event.correlationId());

        try{
            processedEventRepo.save(processedEvent);
        }
        catch (DataIntegrityViolationException exception){
            return;
        }

        boolean stockAvailable = event.quantity() <=5;

        String eventType = stockAvailable ? "INVENTORY_RESERVED" : "INVENTORY_FAILED";

        UUID eventId = UUID.randomUUID();

        InventoryEvent responseEvent = new InventoryEvent(
                eventId,
                eventType,
                event.userId(),
                event.orderId(),
                event.correlationId(),
                "EV1"
        );

        String payload = objectMapper.writeValueAsString(responseEvent);

        Outbox outbox = new Outbox();
        outbox.setOutboxId(UUID.randomUUID());
        outbox.setEventId(eventId);
        outbox.setEventType(eventType);
        outbox.setPayload(payload);
        outbox.setStatus(OutboxStatus.PENDING);

        outboxRepo.save(outbox);

    }

}
