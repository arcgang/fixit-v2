package com.example.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MachineRepository extends JpaRepository<MachineEntity, UUID> {

    Page<MachineEntity> findAllByActive(boolean active, Pageable pageable);

    Optional<MachineEntity> findByMachineIdAndActiveTrue(UUID machineId);
}
