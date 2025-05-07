package com.solution.Ongi.domain.meal.repository;

import com.solution.Ongi.domain.meal.MealSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealScheduleRepository extends JpaRepository<MealSchedule,Long> {
    List<MealSchedule> findByMeal_User_IdAndMealScheduleDate(Long userId, LocalDate mealScheduleDate);
    List<MealSchedule> findByMeal_User_IdAndMealScheduleDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<MealSchedule> findByMeal_User_Id(Long userId);
}
