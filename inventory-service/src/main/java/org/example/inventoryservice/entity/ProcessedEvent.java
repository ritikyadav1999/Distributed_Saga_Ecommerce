package org.example.inventoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
public class ProcessedEvent {

    @Id
    @Column(nullable = false, unique = true,name = "event_id")
    private UUID eventId;

    @Column(nullable = false,name = "correlation_id")
    private String correlationId;

    @Column(nullable = false , name = "processed_at")
    @CreationTimestamp
    private Instant processedAt;

}
