package org.example.inventoryservice.listener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inventoryservice.event.OrderCreatedEvent;
import org.example.inventoryservice.service.InventoryService;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class InventoryEventListener implements StreamListener<String, MapRecord<String,String,String>>   {

    private static final String STREAM_NAME = "order-events";
    private static final String GROUP_NAME = "inventory-group";

    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try{
            String payload = message.getValue().get("payload");

//            Deserialize event
            OrderCreatedEvent event = objectMapper.readValue(payload,OrderCreatedEvent.class);

//            Business Logic
            inventoryService.processOrder(event);

//            ACK only after successful processing
            redisTemplate.opsForStream()
                    .acknowledge(STREAM_NAME,GROUP_NAME,message.getId());

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
