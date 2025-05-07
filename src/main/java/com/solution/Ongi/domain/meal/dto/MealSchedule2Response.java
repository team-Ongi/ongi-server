package com.solution.Ongi.domain.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.MealSchedule2;
import com.solution.Ongi.domain.meal.enums.MealType;

import java.time.LocalDate;
import java.time.LocalTime;

public record MealSchedule2Response(
        Long scheduleId,
        LocalDate mealScheduleDate,
        LocalTime mealScheduleTime,
        boolean status,
        Long mealId
) {
    public static MealSchedule2Response from(MealSchedule2 mealSchedule2) {
        return new MealSchedule2Response(
                mealSchedule2.getId(),
                mealSchedule2.getMealScheduleDate(),
                mealSchedule2.getMealScheduleTime(),
                mealSchedule2.isStatus(),
                mealSchedule2.getMeal().getId()
        );
    }
}
