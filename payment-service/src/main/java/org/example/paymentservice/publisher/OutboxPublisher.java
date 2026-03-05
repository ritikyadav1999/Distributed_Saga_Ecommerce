package org.example.paymentservice.publisher;

import ch.qos.logback.core.util.FixedDelay;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.paymentservice.entity.Outbox;
import org.example.paymentservice.entity.OutboxStatus;
import org.example.paymentservice.repository.OutboxRepo;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OutboxPublisher {

    private final OutboxRepo outboxRepo;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedDelay=2000)
    @Transactional
    public void publish(){
        List<Outbox> batch = outboxRepo.fetchBatch(20).orElseThrow(() -> new RuntimeException("No outbox found!"));

        for(Outbox outbox : batch){

            try{
                Map<String,String> map = Map.of(
                        "payload",outbox.getPayload()
                );

                System.out.println(outbox.getPayload());


                RecordId id = redisTemplate.opsForStream()
                        .add(StreamRecords.mapBacked(map).withStreamKey("payment-events"));

                System.out.println("id: " + id);

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
