package org.example.orderservice.repository;

import org.example.orderservice.enity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID> {

    Optional<Order> findById(UUID orderId);
}
