package com.solution.Ongi.domain.medication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.medication.enums.IntakeTiming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateMealBasedMedicationFromFastAPIRequest(
        @NotBlank
        @JsonProperty("medication_name")
        String medicationName,

        @NotNull
        @JsonProperty("intake_timing")
        IntakeTiming intakeTiming,

        @NotEmpty
        @JsonProperty("meal_type_list")
        List<MealType> mealTypeList,

        @NotNull
        @JsonProperty("remind_after_minutes")
        Integer remindAfterMinutes
) {
}
