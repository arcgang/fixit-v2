package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequestEntity, UUID> {

    @Query("SELECT COUNT(r) FROM MaintenanceRequestEntity r WHERE r.referenceNumber LIKE :prefix")
    long countByReferenceDatePrefix(@Param("prefix") String prefix);
}
