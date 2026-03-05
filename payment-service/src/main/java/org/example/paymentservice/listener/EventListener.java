package org.example.paymentservice.listener;

import lombok.RequiredArgsConstructor;
import org.example.paymentservice.event.InventoryEvent;
import org.example.paymentservice.service.PaymentService;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class EventListener implements StreamListener<String, MapRecord<String,String,String>> {

    private final ObjectMapper  objectMapper;
    private final RedisTemplate<String,String> redisTemplate;
    private final PaymentService paymentService;

    private static final String GROUP_NAME = "payment-group";
    private static final String STREAM_NAME = "inventory_events";


    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        try{
            String payload = message.getValue().get("payload");

            InventoryEvent event = objectMapper.readValue(payload, InventoryEvent.class);
            paymentService.process(event);

            redisTemplate.opsForStream().acknowledge(STREAM_NAME,GROUP_NAME,message.getId());

        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
