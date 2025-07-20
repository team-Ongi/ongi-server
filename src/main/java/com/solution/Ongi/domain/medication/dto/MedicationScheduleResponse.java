package com.solution.Ongi.domain.medication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solution.Ongi.domain.medication.MedicationSchedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record MedicationScheduleResponse(
        Long scheduleId,
        LocalDate checkDate,
        String medicationName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime medicationTime,
        Boolean isTaken,
        String notTakenReason
) {
    public static MedicationScheduleResponse from(MedicationSchedule schedule) {
        return new MedicationScheduleResponse(
                schedule.getId(),
                schedule.getScheduledDate(),
                schedule.getMedication().getMedicationName(),
                schedule.getScheduledTime(),
                schedule.isStatus(),
                schedule.getNotTakenReason()
        );
    }
}

