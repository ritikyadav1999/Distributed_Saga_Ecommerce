package org.example.paymentservice.repository;

import org.example.paymentservice.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepo extends JpaRepository<ProcessedEvent, UUID> {
}
