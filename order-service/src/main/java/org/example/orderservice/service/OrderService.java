package org.example.orderservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.CreateOrderRequest;
import org.example.orderservice.enity.*;
import org.example.orderservice.events.InventoryEvent;
import org.example.orderservice.events.OrderCreatedEvent;
import org.example.orderservice.repository.IdempotencyRecordRepo;
import org.example.orderservice.repository.OrderRepo;
import org.example.orderservice.repository.OutboxRepo;
import org.example.orderservice.repository.ProcessedEventsRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final IdempotencyRecordRepo  idempotencyRecordRepo;
    private final ProcessedEventsRepo processedEventsRepo;
    private final OrderRepo orderRepo;
    private final OutboxRepo outboxRepo;
    private final ObjectMapper objectMapper;


    @Transactional
    public Order createorder(CreateOrderRequest request,String key,String correlationId){

        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(key);
        record.setUserId(request.userId());

        Order order ;

        try{
            idempotencyRecordRepo.save(record);

            order = new Order();
            UUID orderId = UUID.randomUUID();
            order.setOrderId(orderId);
            order.setOrderStatus(OrderStatus.CREATED);
            order.setUserId(request.userId());
            order.setOrderItemId(request.orderItemId());
            order.setQuantity(request.quantity());
            order.setPrice(request.price());
            order.setCorrelationId(correlationId);
            order.setAmount(request.price().multiply(BigDecimal.valueOf(request.quantity())));

            UUID outboxId = UUID.randomUUID();
            UUID eventId = UUID.randomUUID();

            Outbox outbox = new Outbox();
            outbox.setOutboxId(outboxId);
            outbox.setOrderId(orderId);
            outbox.setUserId(request.userId());
            outbox.setEventId(eventId);
            outbox.setEventType("ORDER_EVENT");
            outbox.setStatus(OutboxStatus.PENDING);

            OrderCreatedEvent event = new OrderCreatedEvent(
                    eventId,
                    "ORDER_EVENT",
                    request.userId(),
                    orderId,
                    request.orderItemId(),
                    request.quantity(),
                    request.price(),
                    correlationId,
                    "EV1"
            );

            String payload  =  objectMapper.writeValueAsString(event);

            outbox.setPayload(payload);
            orderRepo.save(order);
            outboxRepo.save(outbox);

//            updating idempotency record with orderId
            record.setOrderId(orderId);
            idempotencyRecordRepo.save(record);

        }
        catch (DataIntegrityViolationException ex){
            IdempotencyRecord retrievedRecord =  idempotencyRecordRepo.findByIdempotencyKeyAndUserId(key,request.userId()).orElseThrow(()->new RuntimeException("Not Found"));
            order  = orderRepo.findById(retrievedRecord.getOrderId()).orElseThrow();

        }

        return order;

    }


    @Transactional
    public void processInventoryEvent(InventoryEvent event){

        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setEventId(event.eventId());
        processedEvent.setCorrelationId(event.correlationId());

        try {
            processedEventsRepo.save(processedEvent);
        }
        catch (DataIntegrityViolationException exception){
            return;
        }

        Order order = orderRepo.findById(event.orderId()).orElseThrow(() -> new RuntimeException("Not Found"));
        if(event.eventType().equals("INVENTORY_RESERVED")){
            order.setOrderStatus(OrderStatus.INVENTORY_RESERVED);
        }
        else {
            order.setOrderStatus(OrderStatus.CANCELLED);
        }
        orderRepo.save(order);
    }

}
