package org.example.inventoryservice.publisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inventoryservice.entity.Outbox;
import org.example.inventoryservice.entity.OutboxStatus;
import org.example.inventoryservice.repository.OutboxRepo;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Component
public class OutboxPublisher {

    private final OutboxRepo  outboxRepo;
    private final RedisTemplate<String,String> redisTemplate;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publish(){

        List<Outbox> outboxes = outboxRepo.fetchBatch(20).orElseThrow(() -> new RuntimeException("No Outbox found!"));

        for (Outbox outbox : outboxes) {

        try{
            System.out.println(outbox.getOutboxId());

            Map<String,String> map = Map.of(
                    "payload",outbox.getPayload()
            );

            redisTemplate.opsForStream().add(StreamRecords.mapBacked(map).withStreamKey("inventory-events"));
            outbox.setStatus(OutboxStatus.SENT);

        } catch (RuntimeException e) {
            outbox.setRetryCount(outbox.getRetryCount()+1);
            if(outbox.getRetryCount()>=5){
                outbox.setStatus(OutboxStatus.FAILED);
            }
        }

        }

    }


}
