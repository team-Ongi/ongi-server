package com.solution.Ongi.domain.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.enums.MealType;

import java.time.LocalTime;

public record MealScheduleResponse(
        Long scheduleId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime mealTime,
        Boolean status,
        MealType mealType
) {
    public static MealScheduleResponse from(MealSchedule schedule) {
        return new MealScheduleResponse(
                schedule.getId(),
                schedule.getMeal_schedule_time(),
                schedule.isStatus(),
                schedule.getMeal().getMeal_type()
        );
    }
}