package com.example.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<MachineEntity, UUID> {

    Page<MachineEntity> findByIsActiveTrue(Pageable pageable);

    Optional<MachineEntity> findByMachineIdAndIsActiveTrue(UUID machineId);
}
