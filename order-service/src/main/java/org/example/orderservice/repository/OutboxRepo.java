package org.example.orderservice.repository;

import org.example.orderservice.enity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxRepo extends JpaRepository<Outbox, UUID> {
}
