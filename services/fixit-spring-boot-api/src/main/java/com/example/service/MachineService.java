package com.example.service;

import com.example.contract.CursorPage;
import com.example.contract.Machine;
import com.example.contract.MachineListResponse;
import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class MachineService {

    private final MachineRepository machineRepository;

    public MachineService(final MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Transactional(readOnly = true)
    public MachineListResponse listMachines(boolean activeOnly, String cursorParam, int limit) {
        var pageable = PageRequest.of(0, limit + 1);
        UUID cursor = decodeCursor(cursorParam);

        List<MachineEntity> rows;
        if (activeOnly) {
            rows = cursor == null
                ? machineRepository.findActiveFirstPage(pageable)
                : machineRepository.findActiveAfterCursor(cursor, pageable);
        } else {
            rows = cursor == null
                ? machineRepository.findAllFirstPage(pageable)
                : machineRepository.findAllAfterCursor(cursor, pageable);
        }

        boolean hasMore = rows.size() > limit;
        List<MachineEntity> page = hasMore ? rows.subList(0, limit) : rows;
        String nextCursor = hasMore ? encodeCursor(page.get(page.size() - 1).getMachineId()) : null;

        List<Machine> data = page.stream().map(this::toDto).toList();
        return new MachineListResponse(data, new CursorPage((long) limit, nextCursor));
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

    private UUID decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) return null;
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursor));
            return UUID.fromString(decoded);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String encodeCursor(UUID id) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(id.toString().getBytes());
    }
}
