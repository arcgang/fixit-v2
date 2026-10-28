package com.example.web;

import com.example.contract.CreateRequestBody;
import com.example.contract.MaintenanceRequest;
import com.example.contract.Priority;
import com.example.contract.RequestStatus;
import com.example.contract.SlaStatus;
import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
import com.example.domain.MaintenanceRequestEntity;
import com.example.domain.MaintenanceRequestRepository;
import com.example.domain.SlaRecordEntity;
import com.example.domain.SlaRecordRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    MachineRepository machineRepository;

    @Mock
    MaintenanceRequestRepository requestRepository;

    @Mock
    SlaRecordRepository slaRecordRepository;

    MeterRegistry meterRegistry;
    RequestService service;

    private static final UUID MACHINE_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID USER_ID    = UUID.fromString("20000000-0000-0000-0000-000000000001");
    private static final Instant FIXED_NOW = Instant.parse("2026-10-28T10:00:00Z");

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        service = new RequestService(machineRepository, requestRepository, slaRecordRepository, meterRegistry);
    }

    @Test
    void createRequest_missingDescription_throwsValidationException() {
        CreateRequestBody body = new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, "");
        RequestService.ValidationException ex = assertThrows(RequestService.ValidationException.class,
                () -> service.createRequest(body, USER_ID, "operator1"));
        assertTrue(ex.getDetails().stream().anyMatch(d -> "description".equals(d.get("field"))),
                "details must contain an entry for 'description'");
    }

    @Test
    void createRequest_nullMachineId_throwsValidationException() {
        CreateRequestBody body = new CreateRequestBody(Priority.CRITICAL, null, "broken");
        RequestService.ValidationException ex = assertThrows(RequestService.ValidationException.class,
                () -> service.createRequest(body, USER_ID, "operator1"));
        assertTrue(ex.getDetails().stream().anyMatch(d -> "machineId".equals(d.get("field"))),
                "details must contain an entry for 'machineId'");
    }

    @Test
    void createRequest_nullPriority_throwsValidationException() {
        CreateRequestBody body = new CreateRequestBody(null, MACHINE_ID, "broken");
        RequestService.ValidationException ex = assertThrows(RequestService.ValidationException.class,
                () -> service.createRequest(body, USER_ID, "operator1"));
        assertTrue(ex.getDetails().stream().anyMatch(d -> "priority".equals(d.get("field"))),
                "details must contain an entry for 'priority'");
    }

    @Test
    void createRequest_inactiveMachine_throwsMachineNotFoundOrInactiveException() {
        CreateRequestBody body = new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, "broken");
        MachineEntity machine = new MachineEntity();
        machine.setMachineId(MACHINE_ID);
        machine.setName("Press A");
        machine.setActive(false);
        machine.setCreatedAt(FIXED_NOW);
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(machine));

        assertThrows(RequestService.MachineNotFoundOrInactiveException.class,
                () -> service.createRequest(body, USER_ID, "operator1"));
    }

    @Test
    void createRequest_machineNotFound_throwsMachineNotFoundOrInactiveException() {
        CreateRequestBody body = new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, "broken");
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.empty());

        assertThrows(RequestService.MachineNotFoundOrInactiveException.class,
                () -> service.createRequest(body, USER_ID, "operator1"));
    }

    @Test
    void createRequest_validCritical_returnsOpenWithCorrectSlaThreshold() {
        CreateRequestBody body = new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, "Pump broken");
        MachineEntity machine = activeMachine("Press A");
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(machine));
        when(requestRepository.lockAndGetDailySequence(any(LocalDate.class))).thenReturn(1);

        MaintenanceRequestEntity savedEntity = stubSavedEntity(body, machine);
        SlaRecordEntity savedSla = stubSlaEntity(savedEntity);
        when(requestRepository.save(any())).thenReturn(savedEntity);
        when(slaRecordRepository.save(any())).thenReturn(savedSla);

        MaintenanceRequest result = service.createRequest(body, USER_ID, "operator1");

        assertEquals(RequestStatus.OPEN, result.status());
        assertEquals(SlaStatus.ON_TRACK, result.slaStatus());
        assertEquals(60L, result.slaThresholdMinutes());
        assertEquals("Press A", result.machineName());
        assertNotNull(result.referenceNumber());
        assertTrue(result.referenceNumber().matches("REQ-\\d{8}-\\d{4}"),
                "referenceNumber must match REQ-YYYYMMDD-NNNN, got: " + result.referenceNumber());
        assertEquals(result.createdAt().plusSeconds(60 * 60), result.slaDeadlineAt());
    }

    @Test
    void createRequest_validMedium_slaThresholdIs240Minutes() {
        CreateRequestBody body = new CreateRequestBody(Priority.MEDIUM, MACHINE_ID, "Pump worn");
        MachineEntity machine = activeMachine("Press B");
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(machine));
        when(requestRepository.lockAndGetDailySequence(any(LocalDate.class))).thenReturn(1);

        MaintenanceRequestEntity savedEntity = stubSavedEntity(body, machine);
        savedEntity.setPriority(com.example.contract.Priority.MEDIUM);
        SlaRecordEntity savedSla = new SlaRecordEntity();
        savedSla.setSlaRecordId(UUID.randomUUID());
        savedSla.setRequestId(savedEntity.getRequestId());
        savedSla.setPriority(com.example.contract.Priority.MEDIUM);
        savedSla.setSlaThresholdMinutes(240);
        savedSla.setSlaDeadlineAt(savedEntity.getCreatedAt().plusSeconds(240 * 60L));
        savedSla.setSlaClockMode(com.example.domain.SlaClockMode.CONTINUOUS);
        savedSla.setCreatedAt(savedEntity.getCreatedAt());
        savedSla.setUpdatedAt(savedEntity.getCreatedAt());
        when(requestRepository.save(any())).thenReturn(savedEntity);
        when(slaRecordRepository.save(any())).thenReturn(savedSla);

        MaintenanceRequest result = service.createRequest(body, USER_ID, "operator1");
        assertEquals(240L, result.slaThresholdMinutes());
    }

    @Test
    void createRequest_successful_incrementsMicrometerCounter() {
        CreateRequestBody body = new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, "Pump broken");
        MachineEntity machine = activeMachine("Press A");
        when(machineRepository.findById(MACHINE_ID)).thenReturn(Optional.of(machine));
        when(requestRepository.lockAndGetDailySequence(any(LocalDate.class))).thenReturn(1);

        MaintenanceRequestEntity savedEntity = stubSavedEntity(body, machine);
        SlaRecordEntity savedSla = stubSlaEntity(savedEntity);
        when(requestRepository.save(any())).thenReturn(savedEntity);
        when(slaRecordRepository.save(any())).thenReturn(savedSla);

        service.createRequest(body, USER_ID, "operator1");

        Counter counter = meterRegistry.find("fixit_requests_created_total")
                .tag("priority", "CRITICAL")
                .counter();
        assertNotNull(counter, "fixit_requests_created_total{priority=CRITICAL} must exist");
        assertEquals(1.0, counter.count(), 0.001);
    }

    private MachineEntity activeMachine(String name) {
        MachineEntity m = new MachineEntity();
        m.setMachineId(MACHINE_ID);
        m.setName(name);
        m.setActive(true);
        m.setCreatedAt(FIXED_NOW);
        return m;
    }

    private MaintenanceRequestEntity stubSavedEntity(CreateRequestBody body, MachineEntity machine) {
        MaintenanceRequestEntity e = new MaintenanceRequestEntity();
        e.setRequestId(UUID.randomUUID());
        e.setMachineId(machine.getMachineId());
        e.setDescription(body.description());
        e.setPriority(body.priority());
        e.setStatus(com.example.contract.RequestStatus.OPEN);
        e.setCreatedByUserId(USER_ID);
        e.setCreatedByDisplayName("operator1");
        e.setCreatedAt(FIXED_NOW);
        e.setUpdatedAt(FIXED_NOW);
        e.setSlaDeadlineAt(FIXED_NOW.plusSeconds(60 * 60L));
        e.setOutOfHoursFlag(false);
        e.setReferenceNumber("REQ-20261028-0001");
        return e;
    }

    private SlaRecordEntity stubSlaEntity(MaintenanceRequestEntity req) {
        SlaRecordEntity s = new SlaRecordEntity();
        s.setSlaRecordId(UUID.randomUUID());
        s.setRequestId(req.getRequestId());
        s.setPriority(req.getPriority());
        s.setSlaThresholdMinutes(60);
        s.setSlaDeadlineAt(req.getSlaDeadlineAt());
        s.setSlaClockMode(com.example.domain.SlaClockMode.CONTINUOUS);
        s.setCreatedAt(req.getCreatedAt());
        s.setUpdatedAt(req.getUpdatedAt());
        return s;
    }
}
