package com.solution.Ongi.domain.meal.repository;

import com.solution.Ongi.domain.meal.MealSchedule2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealSchedule2Repository extends JpaRepository<MealSchedule2,Long> {
    List<MealSchedule2> findByMeal_User_IdAndMealScheduleDate(Long userId, LocalDate mealScheduleDate);
    List<MealSchedule2> findByMeal_User_IdAndMealScheduleDateBetween(Long userId,LocalDate startDate, LocalDate endDate);
}
