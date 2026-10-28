package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequestEntity, UUID> {

    long countByReferenceNumberStartingWith(String prefix);
}
