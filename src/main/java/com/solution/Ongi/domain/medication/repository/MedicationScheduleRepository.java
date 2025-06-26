package com.solution.Ongi.domain.medication.repository;

import com.solution.Ongi.domain.medication.MedicationSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, Long> {
    @Query("""
    select ms from MedicationSchedule ms
    join fetch ms.medication m
    where m.user.id = :userId
    and ms.scheduleDate between :startDate and :endDate
    order by ms.scheduleDate asc, ms.scheduleTime asc
""")
    List<MedicationSchedule> findByUserAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
    select ms from MedicationSchedule ms
    join fetch ms.medication m
    where m.user.id = :userId
    and ms.scheduleDate = :date
    order by ms.scheduleTime asc
""")
    List<MedicationSchedule> findByUserAndDate(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );

    @Query("""
    select ms from MedicationSchedule ms
    join fetch ms.medication m
    where m.user.id = :userId
      and ms.scheduleDate = :date
      and ms.isTaken = false
    order by ms.scheduleTime asc
""")
    List<MedicationSchedule> findNotTakenByUserAndDate(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );

    //TODO: match format
    Optional<MedicationSchedule>
    findFirstByMedication_User_IdAndScheduleDateAndIsTakenFalseAndScheduleTimeAfterOrderByScheduleTimeAsc(
            Long userId, LocalDate date, LocalTime time
    );
}
