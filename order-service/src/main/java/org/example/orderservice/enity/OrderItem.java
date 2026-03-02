package org.example.orderservice.enity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @NotNull
    @Column(name = "quantity",nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "product_id",nullable = false)
    private UUID productId;

    @NotNull
    @Column(name = "price_at_order_time",nullable = false)
    private BigDecimal priceAtOrderTime;

}
