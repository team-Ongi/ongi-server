package com.solution.Ongi.domain.medication.dto;

import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.medication.enums.IntakeTiming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateMealBasedMedicationRequest(
    @NotBlank
    String medicationName,

    @NotNull
    IntakeTiming intakeTiming,

    @NotEmpty
    List<MealType> mealTypeList,

    @NotNull
    Integer remindAfterMinutes
) {

}
