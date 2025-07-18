package com.solution.Ongi.domain.meal.repository;

import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.user.repository.projection.NotTakenMealStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MealScheduleRepository extends JpaRepository<MealSchedule,Long> {
    List<MealSchedule> findByMeal_User_IdAndMealScheduleDate(Long userId, LocalDate mealScheduleDate);
    List<MealSchedule> findByMeal_User_IdAndMealScheduleDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<MealSchedule> findByMeal_User_Id(Long userId);
    //As of today's date, one of the earliest schedules that has 'status=false' since the current time
    Optional<MealSchedule>
        findFirstByMeal_User_IdAndMealScheduleDateAndStatusFalseAndMealScheduleTimeAfterOrderByMealScheduleTimeAsc(
            Long userId, LocalDate date, LocalTime time
        );
    List<LocalDate>
    findDistinctMealScheduleDateByMeal_User_IdAndMealScheduleDateBetweenAndStatusFalse(
            Long userId, LocalDate startDate, LocalDate endDate);

    @Query(value = """
        select
            ms.meal_schedule_date AS date,
            ms.status AS status
        FROM meal_schedule ms
        JOIN meal m ON ms.meal_id = m.meal_id
        WHERE m.user_id = :userId
          AND ms.meal_schedule_date BETWEEN :startDate AND :endDate
        GROUP BY ms.meal_schedule_date
        ORDER BY ms.meal_schedule_date
    """, nativeQuery = true)
    List<NotTakenMealStatusProjection> getNotTakenStatusByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
