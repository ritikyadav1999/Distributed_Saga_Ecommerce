package org.example.orderservice.repository;

import org.example.orderservice.enity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxRepo extends JpaRepository<Outbox, UUID> {

    @Query(value = """
        SELECT * FROM outbox
        WHERE status = 'PENDING' 
        ORDER BY created_at ASC
        LIMIT :batch
""",nativeQuery = true)
    Optional<List<Outbox>> fetchBatch(@Param("batch") int batch);

}
