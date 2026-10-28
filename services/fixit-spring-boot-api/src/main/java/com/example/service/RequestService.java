package com.example.service;

import com.example.contract.*;
import com.example.domain.*;
import com.example.exception.MachineNotFoundOrInactiveException;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RequestService {

    private static final Logger log = LoggerFactory.getLogger(RequestService.class);

    private final MachineRepository machineRepository;
    private final MaintenanceRequestRepository requestRepository;
    private final SlaRecordRepository slaRecordRepository;
    private final ReferenceNumberGenerator referenceNumberGenerator;
    private final EntityManager entityManager;

    public RequestService(
            final MachineRepository machineRepository,
            final MaintenanceRequestRepository requestRepository,
            final SlaRecordRepository slaRecordRepository,
            final ReferenceNumberGenerator referenceNumberGenerator,
            final EntityManager entityManager) {
        this.machineRepository = machineRepository;
        this.requestRepository = requestRepository;
        this.slaRecordRepository = slaRecordRepository;
        this.referenceNumberGenerator = referenceNumberGenerator;
        this.entityManager = entityManager;
    }

    @Transactional
    public MaintenanceRequest createRequest(
            CreateRequestBody body,
            UUID callerUserId,
            String callerDisplayName) {

        MachineEntity machine = machineRepository.findByMachineIdAndIsActiveTrue(body.machineId())
                .orElseThrow(() -> new MachineNotFoundOrInactiveException(
                        "Machine not found or inactive: " + body.machineId()));

        Instant now = Instant.now();
        int slaMinutes = slaThresholdFor(body.priority());
        Instant slaDeadline = now.plusSeconds((long) slaMinutes * 60);

        MaintenanceRequestEntity request = new MaintenanceRequestEntity();
        request.setMachineId(machine.getMachineId());
        request.setDescription(body.description());
        request.setPriority(body.priority());
        request.setStatus(RequestStatus.OPEN);
        request.setCreatedByUserId(callerUserId);
        request.setCreatedByDisplayName(callerDisplayName);
        request.setCreatedAt(now);
        request.setUpdatedAt(now);
        request.setSlaDeadlineAt(slaDeadline);
        request.setOutOfHoursFlag(false);

        // Retry on unique-constraint collision (TOCTOU: two concurrent requests on the same day
        // may generate the same reference number before either is committed).
        DataIntegrityViolationException lastViolation = null;
        for (int attempt = 0; attempt < 5; attempt++) {
            request.setReferenceNumber(referenceNumberGenerator.generate(now));
            try {
                request = requestRepository.save(request);
                requestRepository.flush();
                lastViolation = null;
                break;
            } catch (DataIntegrityViolationException e) {
                lastViolation = e;
                entityManager.clear();
                request = new MaintenanceRequestEntity();
                request.setMachineId(machine.getMachineId());
                request.setDescription(body.description());
                request.setPriority(body.priority());
                request.setStatus(RequestStatus.OPEN);
                request.setCreatedByUserId(callerUserId);
                request.setCreatedByDisplayName(callerDisplayName);
                request.setCreatedAt(now);
                request.setUpdatedAt(now);
                request.setSlaDeadlineAt(slaDeadline);
                request.setOutOfHoursFlag(false);
            }
        }
        if (lastViolation != null) throw lastViolation;

        SlaRecordEntity slaRecord = new SlaRecordEntity();
        slaRecord.setRequestId(request.getRequestId());
        slaRecord.setPriority(body.priority());
        slaRecord.setSlaThresholdMinutes(slaMinutes);
        slaRecord.setSlaDeadlineAt(slaDeadline);
        slaRecord.setSlaClockMode(SlaClockMode.CONTINUOUS);
        slaRecord.setCreatedAt(now);
        slaRecord.setUpdatedAt(now);

        slaRecordRepository.save(slaRecord);

        log.info("action=REQUEST_CREATED request_id={} actor={} reference_number={} outcome=SUCCESS",
                request.getRequestId(), callerUserId, request.getReferenceNumber());

        return toDto(request, machine.getName(), slaMinutes);
    }

    private int slaThresholdFor(Priority priority) {
        return switch (priority) {
            case CRITICAL -> 60;
            case MEDIUM -> 240;
            case LOW -> 480;
        };
    }

    private MaintenanceRequest toDto(MaintenanceRequestEntity r, String machineName, int slaThresholdMinutes) {
        return new MaintenanceRequest(
                r.getStatus(),
                r.getPriority(),
                r.getCreatedAt(),
                r.getMachineId(),
                r.getRequestId(),
                SlaStatus.ON_TRACK,
                r.getAssignedAt(),
                r.getResolvedAt(),
                r.getSlaOutcome(),
                r.getDescription(),
                machineName,
                r.getSlaDeadlineAt(),
                r.getWorkStartedAt(),
                r.getOutOfHoursFlag(),
                r.getResolutionNote(),
                r.getCreatedByUserId(),
                r.getReferenceNumber(),
                r.getAssignedToUserId(),
                (long) slaThresholdMinutes,
                r.getCreatedByDisplayName(),
                r.getAssignedToDisplayName()
        );
    }
}
