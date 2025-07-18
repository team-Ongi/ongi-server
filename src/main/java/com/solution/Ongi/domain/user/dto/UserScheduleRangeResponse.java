package com.solution.Ongi.domain.user.dto;

import java.util.List;

public record UserScheduleRangeResponse(
        List<String> notTakenMedicationDates,
        List<String> notTakenMealDates
) {
}
