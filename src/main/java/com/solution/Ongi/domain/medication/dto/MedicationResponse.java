package com.solution.Ongi.domain.medication.dto;

import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.enums.IntakeTiming;
import com.solution.Ongi.domain.medication.enums.MedicationType;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record MedicationResponse(
    Long id,
    String medicationName,
    MedicationType type,
    List<String> timeList, // 정시복용
    IntakeTiming intakeTiming, // 식사 기반
    List<MealType> mealTypeList, //식사 기반
    Integer remindAfterMinutes // 식사 기반
) {
    public static MedicationResponse from(Medication medication, List<LocalTime> times, List<MealType> mealTypes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return new MedicationResponse(
            medication.getId(),
            medication.getMedicationName(),
            medication.getMedicationType(),
            times.stream()
                .map(time -> time.format(formatter))
                .toList(),
            medication.getIntakeTiming(),
            mealTypes,
            medication.getRemindAfterMinutes()
        );
    }
}