package org.example.orderservice.enity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @NotNull
    @Column(name = "user_id",nullable = false)
    private UUID userId;

    @Column(name = "amount",nullable = false)
    @NotNull
    private BigDecimal amount;

    @Column(name = "created_at",nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status",nullable = false)
    @NotNull
    private OrderStatus orderStatus;

    @NotNull
    @Column(name = "correlation_id",nullable = false)
    private String correlationId;

    @Column(name = "order_item")
    @NotNull
    private UUID orderItemId;

    @NotNull
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull
    private BigDecimal price;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

}
