package com.example.web;

import com.example.contract.CreateRequestBody;
import com.example.contract.MaintenanceRequest;
import com.example.contract.Priority;
import com.example.contract.RequestStatus;
import com.example.contract.SlaOutcome;
import com.example.contract.SlaStatus;
import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
import com.example.domain.MaintenanceRequestEntity;
import com.example.domain.MaintenanceRequestRepository;
import com.example.domain.SlaClockMode;
import com.example.domain.SlaRecordEntity;
import com.example.domain.SlaRecordRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RequestService {

    private static final Logger log = LoggerFactory.getLogger(RequestService.class);
    private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private final MachineRepository machineRepository;
    private final MaintenanceRequestRepository requestRepository;
    private final SlaRecordRepository slaRecordRepository;
    private final MeterRegistry meterRegistry;

    public RequestService(
            MachineRepository machineRepository,
            MaintenanceRequestRepository requestRepository,
            SlaRecordRepository slaRecordRepository,
            MeterRegistry meterRegistry) {
        this.machineRepository = machineRepository;
        this.requestRepository = requestRepository;
        this.slaRecordRepository = slaRecordRepository;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public MaintenanceRequest createRequest(CreateRequestBody body, UUID actorId, String actorDisplayName) {
        validate(body);

        MachineEntity machine = machineRepository.findById(body.machineId())
                .filter(MachineEntity::isActive)
                .orElseThrow(MachineNotFoundOrInactiveException::new);

        Instant now = Instant.now();
        int thresholdMinutes = thresholdFor(body.priority());
        Instant slaDeadline = now.plusSeconds((long) thresholdMinutes * 60);

        LocalDate today = LocalDate.ofInstant(now, ZoneOffset.UTC);
        requestRepository.incrementDailySequence(today);
        int seq = requestRepository.lockAndGetDailySequence(today);
        String referenceNumber = "REQ-" + YYYYMMDD.format(now) + "-" + String.format("%04d", seq);

        MaintenanceRequestEntity entity = new MaintenanceRequestEntity();
        entity.setRequestId(UUID.randomUUID());
        entity.setMachineId(body.machineId());
        entity.setDescription(body.description());
        entity.setPriority(body.priority());
        entity.setStatus(RequestStatus.OPEN);
        entity.setCreatedByUserId(actorId);
        entity.setCreatedByDisplayName(actorDisplayName);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setSlaDeadlineAt(slaDeadline);
        entity.setOutOfHoursFlag(false);
        entity.setReferenceNumber(referenceNumber);

        entity = requestRepository.save(entity);

        SlaRecordEntity sla = new SlaRecordEntity();
        sla.setSlaRecordId(UUID.randomUUID());
        sla.setRequestId(entity.getRequestId());
        sla.setPriority(body.priority());
        sla.setSlaThresholdMinutes(thresholdMinutes);
        sla.setSlaDeadlineAt(slaDeadline);
        sla.setSlaClockMode(SlaClockMode.CONTINUOUS);
        sla.setCreatedAt(now);
        sla.setUpdatedAt(now);
        sla = slaRecordRepository.save(sla);

        MDC.put("request_id", entity.getRequestId().toString());
        MDC.put("actor", actorId.toString());
        MDC.put("action", "REQUEST_CREATED");
        MDC.put("outcome", "SUCCESS");
        try {
            log.info("action=REQUEST_CREATED outcome=SUCCESS request_id={} actor={}", entity.getRequestId(), actorId);
        } finally {
            MDC.remove("request_id");
            MDC.remove("actor");
            MDC.remove("action");
            MDC.remove("outcome");
        }

        meterRegistry.counter("fixit_requests_created_total", "priority", body.priority().name()).increment();

        return toContract(entity, sla, machine.getName());
    }

    private void validate(CreateRequestBody body) {
        List<Map<String, Object>> details = new ArrayList<>();
        if (body.machineId() == null) {
            details.add(Map.of("field", "machineId", "message", "must not be null"));
        }
        if (body.priority() == null) {
            details.add(Map.of("field", "priority", "message", "must not be null"));
        }
        if (body.description() == null || body.description().isBlank()) {
            details.add(Map.of("field", "description", "message", "must not be blank"));
        }
        if (!details.isEmpty()) {
            throw new ValidationException(details);
        }
    }

    private int thresholdFor(Priority priority) {
        return switch (priority) {
            case CRITICAL -> 60;
            case MEDIUM   -> 240;
            case LOW      -> 480;
        };
    }

    private MaintenanceRequest toContract(MaintenanceRequestEntity e, SlaRecordEntity sla, String machineName) {
        return new MaintenanceRequest(
                e.getStatus(),
                e.getPriority(),
                e.getCreatedAt(),
                e.getMachineId(),
                e.getRequestId(),
                SlaStatus.ON_TRACK,
                e.getAssignedAt(),
                e.getResolvedAt(),
                e.getSlaOutcome(),
                e.getDescription(),
                machineName,
                e.getSlaDeadlineAt(),
                e.getWorkStartedAt(),
                e.isOutOfHoursFlag(),
                e.getResolutionNote(),
                e.getCreatedByUserId(),
                e.getReferenceNumber(),
                e.getAssignedToUserId(),
                (long) sla.getSlaThresholdMinutes(),
                e.getCreatedByDisplayName(),
                e.getAssignedToDisplayName()
        );
    }

    public static class ValidationException extends RuntimeException {
        private final List<Map<String, Object>> details;

        public ValidationException(List<Map<String, Object>> details) {
            super("Validation failed");
            this.details = details;
        }

        public List<Map<String, Object>> getDetails() { return details; }
    }

    public static class MachineNotFoundOrInactiveException extends RuntimeException {
        public MachineNotFoundOrInactiveException() {
            super("Machine not found or inactive");
        }
    }
}
