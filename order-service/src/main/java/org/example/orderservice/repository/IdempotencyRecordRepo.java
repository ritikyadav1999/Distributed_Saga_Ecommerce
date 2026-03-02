package org.example.orderservice.repository;

import org.example.orderservice.enity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRecordRepo extends JpaRepository<IdempotencyRecord, UUID> {

    @Query(value = """
            SELECT * FROM 
            idempotency_records
            WHERE idempotency_key = :idempotency_key AND user_id = :user_id
""" , nativeQuery = true)
    Optional<IdempotencyRecord> findByIdempotencyKeyAndUserId(@Param("idempotency_key") String idempotency_key, @Param("user_id") UUID userId);
}
