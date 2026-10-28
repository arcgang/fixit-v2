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

    private final MachineRepository machineRepository;

    public MachineService(final MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Transactional(readOnly = true)
    public MachineListResponse listMachines(boolean activeOnly, String cursor, int limit) {
        int cappedLimit = Math.min(limit, 200);
        PageRequest pageRequest = PageRequest.of(0, cappedLimit, Sort.by(Sort.Direction.ASC, "createdAt"));

        Page<MachineEntity> page;
        if (activeOnly) {
            page = machineRepository.findAllByActive(true, pageRequest);
        } else {
            page = machineRepository.findAll(pageRequest);
        }

        List<Machine> data = page.getContent().stream()
                .map(this::toDto)
                .toList();

        String nextCursor = page.hasNext() ? String.valueOf(cappedLimit) : null;
        CursorPage cursorPage = new CursorPage((long) cappedLimit, nextCursor);
        return new MachineListResponse(data, cursorPage);
    }

    private Machine toDto(MachineEntity e) {
        return new Machine(
                e.getName(),
                e.isActive(),
                e.getLocation(),
                e.getCreatedAt(),
                e.getMachineId(),
                e.getDeactivatedAt(),
                e.getAssetReference(),
                e.getDeactivatedByUserId()
        );
    }
}
