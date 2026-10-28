package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequestEntity, UUID> {

    @Query("SELECT COUNT(r) FROM MaintenanceRequestEntity r WHERE r.referenceNumber LIKE :prefix%")
    long countByReferencePrefixLike(@Param("prefix") String prefix);
}
