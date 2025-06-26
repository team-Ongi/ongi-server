package com.solution.Ongi.domain.schedule.dto;

import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpcomingScheduleResponse(
        Long scheduleId,
        String type,
        LocalDate date,
        LocalTime time,
        boolean done,
        String title,
        Integer currentIgnoreCnt
) {
    public UpcomingScheduleResponse (MealSchedule mealSchedule){
        this(
                mealSchedule.getId(),
                "MEAL",
                mealSchedule.getMealScheduleDate(),
                mealSchedule.getMealScheduleTime(),
                mealSchedule.isStatus(),
                mealSchedule.getMeal().getMealType().name(),
                mealSchedule.getMeal()
                        .getUser()
                        .getCurrentIgnoreCnt()
        );
    }

    public UpcomingScheduleResponse (MedicationSchedule medicationSchedule){
        this(
                medicationSchedule.getId(),
                "MEDICATION",
                medicationSchedule.getScheduleDate(),
                medicationSchedule.getScheduleTime(),
                medicationSchedule.isTaken(),
                medicationSchedule.getMedication().getMedicationName(),
                medicationSchedule
                        .getMedication()
                        .getUser()
                        .getCurrentIgnoreCnt()
        );
    }
}
