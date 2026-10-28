package com.example.service;

import com.example.domain.MaintenanceRequestRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class ReferenceNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private final MaintenanceRequestRepository requestRepository;

    public ReferenceNumberGenerator(final MaintenanceRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public String generate(Instant createdAt) {
        String datePart = DATE_FORMAT.format(createdAt);
        long count = requestRepository.countByReferenceDatePrefix("REQ-" + datePart + "-%");
        return String.format("REQ-%s-%04d", datePart, count + 1);
    }
}
