package org.example.orderservice.enity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "outbox",
        indexes = {
                @Index(name = "idx_event_id",columnList = "event_id"),
                @Index(name = "idx_outbox_status",columnList = "status")
        })

public class Outbox {

    @Id
    @Column(name = "outbox_id")
    private UUID outboxId;

    @NotNull
    @Column(name = "order_id",nullable = false)
    private UUID orderId;    // one order can emit multiple events

    @NotNull
    @Column(name = "user_id",nullable = false)
    private UUID userId;

    @Column(name = "event_id",unique = true,nullable = false)
    private UUID eventId;

    @NotBlank
    @Column(name = "event_type",nullable = false)
    private String eventType;

    @NotBlank
    @Column(name = "payload",columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at",nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "retry_count")
    private int retryCount;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status",nullable = false)
    private OutboxStatus status;

    @PrePersist
    public void setDefaults(){
        retryCount = 0;
    }

}


