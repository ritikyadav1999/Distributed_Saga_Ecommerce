package org.example.orderservice.listener;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.events.InventoryEvent;
import org.example.orderservice.service.OrderService;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
public class EventListener implements StreamListener<String, MapRecord<String,String,String>> {

    private static final String STREAM_NAME = "inventory-events";
    private static final String GROUP_NAME = "order-group";

    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try{
            String payload = message.getValue().get("payload");

            InventoryEvent inventoryEvent = objectMapper.readValue(payload, InventoryEvent.class);

            orderService.processInventoryEvent(inventoryEvent);

            redisTemplate.opsForStream()
                    .acknowledge(STREAM_NAME,GROUP_NAME,message.getId());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
