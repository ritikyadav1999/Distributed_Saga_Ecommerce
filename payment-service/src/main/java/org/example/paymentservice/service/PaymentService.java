package org.example.paymentservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.paymentservice.entity.Outbox;
import org.example.paymentservice.entity.OutboxStatus;
import org.example.paymentservice.entity.ProcessedEvent;
import org.example.paymentservice.event.InventoryEvent;
import org.example.paymentservice.event.PaymentEvent;
import org.example.paymentservice.repository.OutboxRepo;
import org.example.paymentservice.repository.ProcessedEventRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final ProcessedEventRepo  processedEventRepo;
    private final ObjectMapper objectMapper;
    private final OutboxRepo outboxRepo;

    @Transactional
    public void process(InventoryEvent event) {

        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setEventId(event.eventId());
        processedEvent.setCorrelationId(event.correlationId());


        try{
            processedEventRepo.save(processedEvent);
        }
        catch (DataIntegrityViolationException exception){
            return;
        }

        Outbox outbox = new Outbox();
        UUID paymentEventId = UUID.randomUUID();

        outbox.setEventId(paymentEventId);
        outbox.setOutboxId(UUID.randomUUID());
        outbox.setEventType("PAYMENT_SUCCESS");
        outbox.setStatus(OutboxStatus.PENDING);

        PaymentEvent paymentEvent = new PaymentEvent(
            paymentEventId,
                "PAYMENT_SUCCESS",
                event.userId(),
                event.orderId(),
                event.correlationId(),
                "EV1"
        );

        String payload = objectMapper.writeValueAsString(paymentEvent);

        outbox.setPayload(payload);
        outboxRepo.save(outbox);


    }

}
