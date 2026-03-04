package org.example.inventoryservice.repository;

import org.example.inventoryservice.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedEventRepo extends JpaRepository<ProcessedEvent, UUID> {
}
