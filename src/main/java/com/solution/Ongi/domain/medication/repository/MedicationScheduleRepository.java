package com.solution.Ongi.domain.medication.repository;

import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.solution.Ongi.domain.user.repository.projection.NotTakenStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, Long> {

    @Query("""
    select ms from MedicationSchedule ms
    join fetch ms.medication m
    where m.user.id = :userId
    and ms.scheduledDate = :date
    order by ms.scheduledTime asc
""")
    List<MedicationSchedule> findByUserAndDate(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );

    //TODO: match format
    Optional<MedicationSchedule>
    findFirstByMedication_User_IdAndScheduledDateAndIsTakenFalseAndScheduledTimeAfterOrderByScheduledTimeAsc(
            Long userId, LocalDate date, LocalTime time
    );
    void deleteByMedication(Medication medication);

    @Query(value = """
        select
            ms.scheduled_date AS date,
            SUM(CASE WHEN ms.is_taken = true THEN 0 ELSE 1 END) AS notTakenCount
        FROM medication_schedule ms
        JOIN medication m ON ms.medication_id = m.medication_id
        WHERE m.user_id = :userId
          AND ms.scheduled_date BETWEEN :startDate AND :endDate
        GROUP BY ms.scheduled_date
        ORDER BY ms.scheduled_date            
    """, nativeQuery = true)
    List<NotTakenStatsProjection> getNotTakenStatsByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
