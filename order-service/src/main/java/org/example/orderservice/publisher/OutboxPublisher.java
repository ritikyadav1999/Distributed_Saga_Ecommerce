package org.example.orderservice.publisher;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.enity.Outbox;
import org.example.orderservice.enity.OutboxStatus;
import org.example.orderservice.repository.OutboxRepo;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.reader.StreamReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OutboxPublisher {

    private final OutboxRepo outboxRepo;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publish() {
        List<Outbox> outboxes = outboxRepo.fetchBatch(20).orElseThrow(RuntimeException::new);
            for(Outbox outbox : outboxes) {

                try{
                    Map<String,String> map = Map.of(
                            "payload",outbox.getPayload()
                    );

                    redisTemplate.opsForStream()
                            .add(StreamRecords.mapBacked(map).withStreamKey("order-events"));

                    outbox.setStatus(OutboxStatus.SENT);

                }
                catch (Exception e){
                    outbox.setRetryCount(outbox.getRetryCount()+1);
                    if(outbox.getRetryCount()>=5){
                        outbox.setStatus(OutboxStatus.FAILED);
                    }
                }

            }
    }


}
