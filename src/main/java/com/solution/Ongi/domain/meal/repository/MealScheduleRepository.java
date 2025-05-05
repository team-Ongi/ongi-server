package com.solution.Ongi.domain.meal.repository;

import com.solution.Ongi.domain.meal.MealSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealScheduleRepository extends JpaRepository<MealSchedule,Long> {
    List<MealSchedule> findByMeal_User_Id(Long userId);
    List<MealSchedule> findByMeal_User_IdAndStatusFalse(Long userId);
}
