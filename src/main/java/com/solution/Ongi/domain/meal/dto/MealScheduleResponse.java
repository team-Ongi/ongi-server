package com.solution.Ongi.domain.meal.dto;

import com.solution.Ongi.domain.meal.MealSchedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record MealScheduleResponse(
        Long scheduleId,
        LocalDate mealScheduleDate,
        LocalTime mealScheduleTime,
        boolean status,
        Long mealId,
        String mealScheduleName
) {
    public static MealScheduleResponse from(MealSchedule mealSchedule) {
        return new MealScheduleResponse(
                mealSchedule.getId(),
                mealSchedule.getScheduledDate(),
                mealSchedule.getScheduledTime(),
                mealSchedule.isStatus(),
                mealSchedule.getMeal().getId(),
                mealSchedule.getMeal().getMealType().name()
        );
    }
}
