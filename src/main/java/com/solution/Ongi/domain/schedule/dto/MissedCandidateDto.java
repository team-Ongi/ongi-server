package com.solution.Ongi.domain.schedule.dto;

import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.schedule.enums.ScheduleType;

import java.time.LocalDate;
import java.time.LocalTime;

public record MissedCandidateDto(
    ScheduleType type,
    Long scheduleId,
    LocalDate scheduledDate,
    LocalTime scheduledTime,
    boolean status
) {
    public static MissedCandidateDto fromMeal(MealSchedule m) {
        return new MissedCandidateDto(
                ScheduleType.MEAL,
                m.getId(),
                m.getScheduledDate(),
                m.getScheduledTime(),
                m.getStatus()
        );
    }

    public static MissedCandidateDto fromMedication(MedicationSchedule m) {
        return new MissedCandidateDto(
                ScheduleType.MEDICATION,
                m.getId(),
                m.getScheduledDate(),
                m.getScheduledTime(),
                m.isStatus()
        );
    }
}