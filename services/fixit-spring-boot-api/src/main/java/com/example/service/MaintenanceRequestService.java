package com.example.service;

import com.example.contract.*;
import com.example.domain.*;
import com.example.web.MachineNotFoundOrInactiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MaintenanceRequestService {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceRequestService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private final MachineRepository machineRepository;
    private final MaintenanceRequestRepository requestRepository;
    private final SlaRecordRepository slaRecordRepository;

    public MaintenanceRequestService(
            final MachineRepository machineRepository,
            final MaintenanceRequestRepository requestRepository,
            final SlaRecordRepository slaRecordRepository) {
        this.machineRepository = machineRepository;
        this.requestRepository = requestRepository;
        this.slaRecordRepository = slaRecordRepository;
    }

    @Transactional
    public MaintenanceRequest createRequest(
            CreateRequestBody body,
            UUID actorUserId,
            String actorDisplayName) {

        MachineEntity machine = machineRepository.findByMachineIdAndActiveTrue(body.machineId())
                .orElseThrow(() -> new MachineNotFoundOrInactiveException(String.valueOf(body.machineId())));

        Instant now = Instant.now();
        int slaMinutes = slaThresholdMinutes(body.priority());
        Instant slaDeadline = now.plusSeconds((long) slaMinutes * 60);

        String referenceNumber = generateReferenceNumber(now);

        MaintenanceRequestEntity entity = new MaintenanceRequestEntity();
        entity.setRequestId(UUID.randomUUID());
        entity.setMachineId(body.machineId());
        entity.setDescription(body.description());
        entity.setPriority(body.priority());
        entity.setStatus(RequestStatus.OPEN);
        entity.setCreatedByUserId(actorUserId);
        entity.setCreatedByDisplayName(actorDisplayName);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setSlaDeadlineAt(slaDeadline);
        entity.setOutOfHoursFlag(false);
        entity.setReferenceNumber(referenceNumber);

        requestRepository.save(entity);

        SlaRecordEntity slaRecord = new SlaRecordEntity();
        slaRecord.setSlaRecordId(UUID.randomUUID());
        slaRecord.setRequestId(entity.getRequestId());
        slaRecord.setPriority(body.priority());
        slaRecord.setSlaThresholdMinutes(slaMinutes);
        slaRecord.setSlaDeadlineAt(slaDeadline);
        slaRecord.setSlaClockMode("CONTINUOUS");
        slaRecord.setCreatedAt(now);
        slaRecord.setUpdatedAt(now);
        slaRecordRepository.save(slaRecord);

        log.atInfo()
                .addKeyValue("request_id", entity.getRequestId())
                .addKeyValue("actor", actorUserId)
                .addKeyValue("action", "REQUEST_CREATED")
                .addKeyValue("outcome", "SUCCESS")
                .addKeyValue("machine_id", body.machineId())
                .addKeyValue("priority", body.priority())
                .log("maintenance request created");

        return toDto(entity, machine, slaMinutes);
    }

    private String generateReferenceNumber(Instant createdAt) {
        String datePart = DATE_FMT.format(createdAt);
        String prefix = "REQ-" + datePart + "-";
        long existing = requestRepository.countByReferenceNumberStartingWith(prefix);
        return prefix + String.format("%04d", existing + 1);
    }

    private int slaThresholdMinutes(Priority priority) {
        return switch (priority) {
            case CRITICAL -> 60;
            case MEDIUM -> 240;
            case LOW -> 480;
        };
    }

    private SlaStatus computeSlaStatus(Instant slaDeadlineAt, Instant now) {
        long secondsRemaining = slaDeadlineAt.getEpochSecond() - now.getEpochSecond();
        if (secondsRemaining < 0) return SlaStatus.BREACHED;
        if (secondsRemaining < 15 * 60) return SlaStatus.AT_RISK;
        return SlaStatus.ON_TRACK;
    }

    private MaintenanceRequest toDto(MaintenanceRequestEntity e, MachineEntity machine, int slaThresholdMinutes) {
        return new MaintenanceRequest(
                e.getStatus(),
                e.getPriority(),
                e.getCreatedAt(),
                e.getMachineId(),
                e.getRequestId(),
                computeSlaStatus(e.getSlaDeadlineAt(), e.getCreatedAt()),
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
                (long) slaThresholdMinutes,
                e.getCreatedByDisplayName(),
                e.getAssignedToDisplayName()
        );
    }
}
