package com.solution.Ongi.domain.meal.dto;

import com.solution.Ongi.domain.meal.MealSchedule2;

import java.time.LocalDate;
import java.time.LocalTime;

public record MealSchedule2WithUserResponse(
        Long scheduleId,
        LocalDate scheduleDate,
        LocalTime mealScheduleTime,
        boolean status,
        Long mealId,
        String userName
) {
}
