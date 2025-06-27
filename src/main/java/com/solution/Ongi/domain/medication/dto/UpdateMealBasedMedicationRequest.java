package com.solution.Ongi.domain.medication.dto;

import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.medication.enums.IntakeTiming;
import java.util.List;

public record UpdateMealBasedMedicationRequest(
    String medicationName,
    IntakeTiming intakeTiming,
    List<MealType> mealTypes,
    Integer remindAfterMinutes,
    Long medicationScheduleId
) {

}
