package org.example.orderservice.listener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.enity.Order;
import org.example.orderservice.enity.OrderStatus;
import org.example.orderservice.enity.ProcessedEvent;
import org.example.orderservice.events.OrderCreatedEvent;
import org.example.orderservice.events.PaymentEvent;
import org.example.orderservice.repository.OrderRepo;
import org.example.orderservice.repository.ProcessedEventsRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
@RequiredArgsConstructor
@Component
public class PaymentEventListener implements StreamListener<String, MapRecord<String,String,String>> {

    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProcessedEventsRepo processedEventsRepo;
    private final OrderRepo  orderRepo;

    @Override
    @Transactional
    public void onMessage(MapRecord<String, String, String> message) {

        String payload = message.getValue().get("payload");
        PaymentEvent paymentEvent = objectMapper.readValue(payload, PaymentEvent.class);

        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setEventId(paymentEvent.eventId());
        processedEvent.setCorrelationId(paymentEvent.correlationId());

        try{
            processedEventsRepo.save(processedEvent);
        }
        catch (DataIntegrityViolationException exception){
            return;
        }

        Order order = orderRepo.findById(paymentEvent.orderId()).orElseThrow();
        if(paymentEvent.eventType().equals("PAYMENT_SUCCESS")){
            order.setOrderStatus(OrderStatus.COMPLETED);
        }
        else
            order.setOrderStatus(OrderStatus.CANCELLED);
    }
}
