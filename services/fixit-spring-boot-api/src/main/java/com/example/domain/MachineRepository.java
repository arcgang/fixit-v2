package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MachineRepository extends JpaRepository<MachineEntity, UUID> {

    @Query("""
            SELECT m FROM MachineEntity m
            WHERE m.isActive = true
              AND (:cursor IS NULL OR m.machineId > :cursor)
            ORDER BY m.createdAt ASC, m.machineId ASC
            LIMIT :limit
            """)
    List<MachineEntity> findActiveAfterCursor(
            @Param("cursor") UUID cursor,
            @Param("limit") int limit);

    @Query("""
            SELECT m FROM MachineEntity m
            WHERE (:cursor IS NULL OR m.machineId > :cursor)
            ORDER BY m.createdAt ASC, m.machineId ASC
            LIMIT :limit
            """)
    List<MachineEntity> findAllAfterCursor(
            @Param("cursor") UUID cursor,
            @Param("limit") int limit);
}
