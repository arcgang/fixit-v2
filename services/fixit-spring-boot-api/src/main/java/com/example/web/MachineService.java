package com.example.web;

import com.example.contract.CursorPage;
import com.example.contract.Machine;
import com.example.contract.MachineListResponse;
import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class MachineService {

    private final MachineRepository machineRepository;

    public MachineService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Transactional(readOnly = true)
    public MachineListResponse listMachines(boolean activeOnly, String cursorParam, int limit, boolean isSupervisor) {
        UUID cursorUuid = decodeCursor(cursorParam);
        int fetchSize = limit + 1;

        List<MachineEntity> rows = activeOnly
                ? machineRepository.findActiveAfterCursor(cursorUuid, fetchSize)
                : machineRepository.findAllAfterCursor(cursorUuid, fetchSize);

        boolean hasMore = rows.size() > limit;
        List<MachineEntity> page = hasMore ? rows.subList(0, limit) : rows;

        String nextCursor = hasMore
                ? encodeCursor(page.get(page.size() - 1).getMachineId())
                : null;

        List<Machine> data = page.stream().map(this::toContract).toList();
        return new MachineListResponse(data, new CursorPage((long) limit, nextCursor));
    }

    private UUID decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        String decoded = new String(Base64.getDecoder().decode(cursor));
        return UUID.fromString(decoded);
    }

    private String encodeCursor(UUID machineId) {
        return Base64.getEncoder().encodeToString(machineId.toString().getBytes());
    }

    private Machine toContract(MachineEntity e) {
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
