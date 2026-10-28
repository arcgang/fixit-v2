package com.example.service;

import com.example.contract.CursorPage;
import com.example.contract.Machine;
import com.example.contract.MachineListResponse;
import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
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
    public MachineListResponse listMachines(boolean activeOnly, int limit, String cursor) {
        int effectiveLimit = Math.min(limit, MAX_LIMIT);
        // Sort by machineId for a stable keyset; cursor is the last machineId from the previous page.
        PageRequest pageRequest = PageRequest.of(0, effectiveLimit, Sort.by("machineId").ascending());

        UUID cursorId = cursor != null ? UUID.fromString(cursor) : null;

        List<MachineEntity> rows;
        if (activeOnly) {
            rows = cursorId != null
                    ? machineRepository.findByIsActiveTrueAndMachineIdGreaterThan(cursorId, pageRequest)
                    : machineRepository.findByIsActiveTrue(pageRequest).getContent();
        } else {
            rows = cursorId != null
                    ? machineRepository.findByMachineIdGreaterThan(cursorId, pageRequest)
                    : machineRepository.findAll(pageRequest).getContent();
        }

        List<Machine> data = rows.stream().map(this::toDto).toList();
        String nextCursor = rows.size() == effectiveLimit ? rows.getLast().getMachineId().toString() : null;
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
