package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MachineRepository extends JpaRepository<MachineEntity, UUID> {
}
