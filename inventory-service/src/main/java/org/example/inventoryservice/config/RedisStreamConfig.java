package org.example.inventoryservice.config;

import lombok.RequiredArgsConstructor;
import org.example.inventoryservice.listener.InventoryEventListener;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Configuration
public class RedisStreamConfig {

    private static final String STREAM_NAME = "order-events";
    private static final String GROUP_NAME = "inventory-group";

    private final InventoryEventListener inventoryEventListener;

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String,String,String>> streamMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory
    ) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,MapRecord<String,String,String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(2))
                        .build();

        return StreamMessageListenerContainer.create(redisConnectionFactory, options);
    }

    @Bean
    public ApplicationRunner streamRunner(StreamMessageListenerContainer<String, MapRecord<String,String,String>> container) {
        return args -> {
            String consumerName = UUID.randomUUID().toString();
            container.receive(
                    Consumer.from(GROUP_NAME,consumerName),
                    StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed()),
                    inventoryEventListener
            );
            container.start();
        };
    }

}
