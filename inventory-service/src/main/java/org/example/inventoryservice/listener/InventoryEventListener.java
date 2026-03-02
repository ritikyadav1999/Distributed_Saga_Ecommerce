package org.example.inventoryservice.listener;

import lombok.RequiredArgsConstructor;
import org.example.inventoryservice.event.OrderCreatedEvent;
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

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try{
            String payload = message.getValue().get("payload");

//            Deserialize event
            OrderCreatedEvent event = objectMapper.readValue(payload,OrderCreatedEvent.class);

//            Business Logic
            System.out.println(event.orderId());

//            ACK only after successful processing
            redisTemplate.opsForStream()
                    .acknowledge(STREAM_NAME,GROUP_NAME,message.getId());

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
