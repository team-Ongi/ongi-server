package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.meal.dto.MealResponse;
import com.solution.Ongi.domain.medication.dto.MedicationResponse;

import java.util.List;

public record UserSchedulesResponse(
        List<MedicationResponse> userMedicationList,
        List<MealResponse> userMealList
) {
}
