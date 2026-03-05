package org.example.paymentservice.config;

import lombok.RequiredArgsConstructor;
import org.example.paymentservice.listener.EventListener;
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
import java.time.Instant;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private static final String GROUP_NAME = "payment-group";
    private static final String STREAM_NAME = "inventory-events";

    private final EventListener eventListener;

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String,String,String>> streamMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory
    ){
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,MapRecord<String,String,String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(2))
                        .build();

                return StreamMessageListenerContainer.create(redisConnectionFactory,options);

    }


    @Bean
    public ApplicationRunner streamRunner(StreamMessageListenerContainer<String, MapRecord<String,String,String>> streamMessageListenerContainer){
        return args -> {
            String consumerName = UUID.randomUUID().toString();
            streamMessageListenerContainer.receive(
                    Consumer.from(GROUP_NAME,consumerName),
                    StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed()),
                    eventListener
            );

            streamMessageListenerContainer.start();
            System.out.println("running");
            };
    }

}
