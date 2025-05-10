package com.solution.Ongi.domain.meal.repository;

import com.solution.Ongi.domain.meal.MealSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
