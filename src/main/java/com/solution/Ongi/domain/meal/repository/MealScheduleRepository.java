package com.solution.Ongi.domain.meal.repository;

import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.user.repository.projection.NotTakenMealStatusProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MealScheduleRepository extends JpaRepository<MealSchedule,Long> {
    List<MealSchedule> findByMeal_User_IdAndScheduledDate(Long userId, LocalDate mealScheduleDate);
    List<MealSchedule> findByMeal_User_IdAndScheduledDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<MealSchedule> findByMeal_User_Id(Long userId);
    Optional<MealSchedule> findFirstByMeal_User_IdAndScheduledDateAndStatusFalseAndScheduledTimeAfterOrderByScheduledTimeAsc(
            Long userId, LocalDate date, LocalTime time
        );
    Optional<MealSchedule> findFirstByMeal_User_IdAndScheduledDateAndScheduledTimeLessThanEqualOrderByScheduledTimeDesc(
            Long userId, LocalDate date, LocalTime time);

    @Query(value = """
        select
            ms.scheduled_date AS date,
            ms.status AS status
        FROM meal_schedule ms
        JOIN meal m ON ms.meal_id = m.meal_id
        WHERE m.user_id = :userId
          AND ms.scheduled_date BETWEEN :startDate AND :endDate
        GROUP BY ms.scheduled_date
        ORDER BY ms.scheduled_date
    """, nativeQuery = true)
    List<NotTakenMealStatusProjection> getNotTakenStatusByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO meal_schedule " +
            "(scheduled_date, scheduled_time, status, meal_id, created_at, updated_at) " +
            "VALUES (:scheduledDate, :scheduledTime, :status, :mealId, NOW(), NOW())",
            nativeQuery = true)
    int saveWithNativeQuery(
            @Param("scheduledDate") LocalDate scheduledDate,
            @Param("scheduledTime") LocalTime scheduledTime,
            @Param("status") boolean status,
            @Param("mealId") Long mealId
    );



}
