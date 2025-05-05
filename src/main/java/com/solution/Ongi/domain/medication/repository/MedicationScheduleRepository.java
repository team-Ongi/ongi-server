package com.solution.Ongi.domain.medication.repository;

import com.solution.Ongi.domain.medication.MedicationSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, Long> {
    @Query("""
    select ms from MedicationSchedule ms
    join fetch ms.medication m
    where m.user.id = :userId
    and ms.checkDate between :startDate and :endDate
    order by ms.checkDate asc, ms.medicationTime asc
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
    and ms.checkDate = :date
    order by ms.medicationTime asc
""")
    List<MedicationSchedule> findByUserAndDate(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );

    @Query("""
    select ms from MedicationSchedule ms
    join fetch ms.medication m
    where m.user.id = :userId
      and ms.checkDate = :date
      and ms.isTaken = false
    order by ms.medicationTime asc
""")
    List<MedicationSchedule> findNotTakenByUserAndDate(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );


}
