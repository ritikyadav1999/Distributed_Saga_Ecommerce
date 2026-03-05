package org.example.paymentservice.repository;

import org.example.paymentservice.entity.Outbox;
import org.example.paymentservice.event.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutboxRepo extends JpaRepository<Outbox, UUID> {

    @Query(value= """
        SELECT * FROM outbox
        WHERE status = 'PENDING'
        ORDER BY created_at ASC
        LIMIT :batch
""" , nativeQuery = true)
    Optional<List<Outbox>> fetchBatch(@Param("batch") int batch);

}
