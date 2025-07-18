package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.meal.dto.MealScheduleResponse;
import com.solution.Ongi.domain.medication.dto.MedicationScheduleResponse;

import java.util.List;

public record UserScheduleOnDateResponse(
        List<MedicationScheduleResponse> userMedicationScheduleList,
        List<MealScheduleResponse> userMealScheudleList
) {
}
