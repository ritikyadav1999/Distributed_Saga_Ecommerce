package org.example.orderservice.repository;

import org.example.orderservice.enity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ProcessedEventsRepo extends JpaRepository<ProcessedEvent, UUID> {
}
