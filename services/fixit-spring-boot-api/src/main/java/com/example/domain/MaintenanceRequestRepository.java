package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.UUID;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequestEntity, UUID> {

    @Modifying
    @Query(value = """
            INSERT INTO request_daily_sequence (seq_date, last_value)
            VALUES (:seqDate, 1)
            ON CONFLICT (seq_date) DO UPDATE SET last_value = request_daily_sequence.last_value + 1
            """, nativeQuery = true)
    void incrementDailySequence(@Param("seqDate") LocalDate seqDate);

    @Query(value = "SELECT last_value FROM request_daily_sequence WHERE seq_date = :seqDate FOR UPDATE",
            nativeQuery = true)
    Integer lockAndGetDailySequence(@Param("seqDate") LocalDate seqDate);
}
