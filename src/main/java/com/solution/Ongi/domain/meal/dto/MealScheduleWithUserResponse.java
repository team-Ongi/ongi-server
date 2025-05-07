package com.solution.Ongi.domain.meal.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record MealScheduleWithUserResponse(
        Long scheduleId,
        LocalDate scheduleDate,
        LocalTime mealScheduleTime,
        boolean status,
        Long mealId,
        String userName
) {
}
