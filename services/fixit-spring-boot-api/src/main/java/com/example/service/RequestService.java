package com.example.service;

import com.example.contract.*;
import com.example.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class RequestService {

    private static final Logger log = LoggerFactory.getLogger(RequestService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final MachineRepository machineRepository;
    private final MaintenanceRequestRepository requestRepository;
    private final SlaRecordRepository slaRecordRepository;

    public RequestService(
            final MachineRepository machineRepository,
            final MaintenanceRequestRepository requestRepository,
            final SlaRecordRepository slaRecordRepository) {
        this.machineRepository = machineRepository;
        this.requestRepository = requestRepository;
        this.slaRecordRepository = slaRecordRepository;
    }

    @Transactional
    public MaintenanceRequest createRequest(
            UUID machineId,
            String description,
            Priority priority,
            UUID actorUserId,
            String actorDisplayName) {

        MachineEntity machine = machineRepository.findByMachineIdAndActiveTrue(machineId)
            .orElseThrow(() -> new MachineNotFoundOrInactiveException(machineId));

        Instant now = Instant.now();
        int slaThresholdMinutes = slaThresholdFor(priority);
        Instant slaDeadlineAt = now.plusSeconds((long) slaThresholdMinutes * 60);
        UUID requestId = UUID.randomUUID();

        String dateStr = DATE_FMT.format(LocalDate.ofInstant(now, ZoneOffset.UTC));
        String prefix = "REQ-" + dateStr + "-";
        long seqSeed = requestRepository.countByReferencePrefixLike(prefix);

        MaintenanceRequestEntity entity = null;
        for (int attempt = 0; attempt < 10; attempt++) {
            String referenceNumber = prefix + String.format("%04d", seqSeed + attempt + 1);
            entity = MaintenanceRequestEntity.create(
                requestId, machine, description, priority,
                actorUserId, actorDisplayName,
                now, slaDeadlineAt, referenceNumber
            );
            try {
                requestRepository.saveAndFlush(entity);
                break;
            } catch (DataIntegrityViolationException e) {
                entity = null;
                requestId = UUID.randomUUID();
            }
        }
        if (entity == null) {
            throw new IllegalStateException("Failed to generate unique reference number after retries");
        }

        SlaRecordEntity slaRecord = SlaRecordEntity.create(entity, priority, slaThresholdMinutes, slaDeadlineAt, now);
        slaRecordRepository.save(slaRecord);

        log.info("action=REQUEST_CREATED request_id={} actor={} priority={} outcome=SUCCESS",
            entity.getRequestId(), actorUserId, priority);

        return toDto(entity, machine);
    }

    private int slaThresholdFor(Priority priority) {
        return switch (priority) {
            case CRITICAL -> 60;
            case MEDIUM -> 240;
            case LOW -> 480;
        };
    }

    private MaintenanceRequest toDto(MaintenanceRequestEntity e, MachineEntity machine) {
        return new MaintenanceRequest(
            e.getStatus(),
            e.getPriority(),
            e.getCreatedAt(),
            machine.getMachineId(),
            e.getRequestId(),
            SlaStatus.ON_TRACK,
            e.getAssignedAt(),
            e.getResolvedAt(),
            e.getSlaOutcome(),
            e.getDescription(),
            machine.getName(),
            e.getSlaDeadlineAt(),
            e.getWorkStartedAt(),
            e.isOutOfHoursFlag(),
            e.getResolutionNote(),
            e.getCreatedByUserId(),
            e.getReferenceNumber(),
            e.getAssignedToUserId(),
            (long) slaThresholdFor(e.getPriority()),
            e.getCreatedByDisplayName(),
            e.getAssignedToDisplayName()
        );
    }
}
