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
@Table(name = "idempotency_records", uniqueConstraints = {
        @UniqueConstraint(name = "uk_idempotence_user_id", columnNames = {"idempotency_key","user_id"})
})
@NoArgsConstructor
@Getter
@Setter
public class IdempotencyRecord {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "idempotency_key", nullable = false)
    @NotBlank
    private String idempotencyKey;

    @Column(name = "user_id",nullable = false)
    @NotNull
    private UUID userId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "created_at",nullable = false)
    @CreationTimestamp
    private Instant createdAt;

}
