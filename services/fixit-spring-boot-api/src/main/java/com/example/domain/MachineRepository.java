package com.example.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MachineRepository extends JpaRepository<MachineEntity, UUID> {

    List<MachineEntity> findAllByActiveAndCreatedAtAfterOrderByCreatedAtAsc(boolean active, Instant after, Pageable pageable);

    List<MachineEntity> findAllByActiveOrderByCreatedAtAsc(boolean active, Pageable pageable);

    List<MachineEntity> findAllByCreatedAtAfterOrderByCreatedAtAsc(Instant after, Pageable pageable);

    Optional<MachineEntity> findByMachineIdAndActiveTrue(UUID machineId);
}
