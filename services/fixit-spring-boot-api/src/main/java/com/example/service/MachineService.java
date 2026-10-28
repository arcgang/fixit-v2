package com.example.service;

import com.example.contract.CursorPage;
import com.example.contract.Machine;
import com.example.contract.MachineListResponse;
import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MachineService {

    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 200;

    private final MachineRepository machineRepository;

    public MachineService(final MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Transactional(readOnly = true)
    public MachineListResponse listMachines(boolean activeOnly, int limit) {
        int effectiveLimit = Math.min(limit, MAX_LIMIT);
        PageRequest pageRequest = PageRequest.of(0, effectiveLimit, Sort.by("name").ascending());

        Page<MachineEntity> page;
        if (activeOnly) {
            page = machineRepository.findByIsActiveTrue(pageRequest);
        } else {
            page = machineRepository.findAll(pageRequest);
        }

        List<Machine> data = page.getContent().stream()
                .map(this::toDto)
                .toList();

        String nextCursor = page.hasNext() ? page.getContent().getLast().getMachineId().toString() : null;
        return new MachineListResponse(data, new CursorPage((long) effectiveLimit, nextCursor));
    }

    private Machine toDto(MachineEntity e) {
        return new Machine(
                e.getName(),
                e.getIsActive(),
                e.getLocation(),
                e.getCreatedAt(),
                e.getMachineId(),
                e.getDeactivatedAt(),
                e.getAssetReference(),
                e.getDeactivatedByUserId()
        );
    }
}
