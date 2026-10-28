package com.example.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MachineRepository extends JpaRepository<MachineEntity, UUID> {

    @Query("SELECT m FROM MachineEntity m WHERE m.active = true AND m.machineId > :cursor ORDER BY m.machineId ASC")
    List<MachineEntity> findActiveAfterCursor(@Param("cursor") UUID cursor, Pageable pageable);

    @Query("SELECT m FROM MachineEntity m WHERE m.active = true ORDER BY m.machineId ASC")
    List<MachineEntity> findActiveFirstPage(Pageable pageable);

    @Query("SELECT m FROM MachineEntity m WHERE m.machineId > :cursor ORDER BY m.machineId ASC")
    List<MachineEntity> findAllAfterCursor(@Param("cursor") UUID cursor, Pageable pageable);

    @Query("SELECT m FROM MachineEntity m ORDER BY m.machineId ASC")
    List<MachineEntity> findAllFirstPage(Pageable pageable);

    Optional<MachineEntity> findByMachineIdAndActiveTrue(UUID machineId);
}
