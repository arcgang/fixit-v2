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

import java.time.Instant;
import java.util.List;

@Service
public class MachineService {

    private final MachineRepository machineRepository;

    public MachineService(final MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Transactional(readOnly = true)
    public MachineListResponse listMachines(boolean activeOnly, String cursor, int limit) {
        int cappedLimit = Math.min(limit, 200);
        // fetch one extra to know if there is a next page
        PageRequest pageRequest = PageRequest.of(0, cappedLimit + 1, Sort.by(Sort.Direction.ASC, "createdAt"));

        Instant afterCursor = cursor != null ? Instant.parse(cursor) : Instant.EPOCH;

        List<MachineEntity> rows;
        if (afterCursor.equals(Instant.EPOCH)) {
            if (activeOnly) {
                rows = machineRepository.findAllByActiveOrderByCreatedAtAsc(true, pageRequest);
            } else {
                rows = machineRepository.findAll(pageRequest).getContent();
            }
        } else {
            if (activeOnly) {
                rows = machineRepository.findAllByActiveAndCreatedAtAfterOrderByCreatedAtAsc(true, afterCursor, pageRequest);
            } else {
                rows = machineRepository.findAllByCreatedAtAfterOrderByCreatedAtAsc(afterCursor, pageRequest);
            }
        }

        boolean hasNext = rows.size() > cappedLimit;
        List<MachineEntity> page = hasNext ? rows.subList(0, cappedLimit) : rows;

        List<Machine> data = page.stream().map(this::toDto).toList();

        String nextCursor = hasNext ? page.get(page.size() - 1).getCreatedAt().toString() : null;
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
