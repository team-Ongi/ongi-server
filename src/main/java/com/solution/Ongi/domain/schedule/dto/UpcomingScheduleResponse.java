package com.solution.Ongi.domain.schedule.dto;

import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.dto.MealResponse;
import com.solution.Ongi.domain.medication.MedicationSchedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpcomingScheduleResponse(
        Long scheduleId,
        String type,
        LocalDate date,
        LocalTime time,
        boolean done,
        String title
) {
    public UpcomingScheduleResponse (MealSchedule mealSchedule){
        this(
                mealSchedule.getId(),
                "MEAL",
                mealSchedule.getMealScheduleDate(),
                mealSchedule.getMealScheduleTime(),
                mealSchedule.isStatus(),
                mealSchedule.getMeal().getMeal_type().name()
        );
    }

    public UpcomingScheduleResponse (MedicationSchedule medicationSchedule){
        this(
                medicationSchedule.getId(),
                "MEDICATION",
                medicationSchedule.getCheckDate(),
                medicationSchedule.getMedicationTime(),
                medicationSchedule.isTaken(),
                medicationSchedule.getMedication().getMedication_title()
        );
    }
}
