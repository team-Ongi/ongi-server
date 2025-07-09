package com.solution.Ongi.domain.user.dto;


import java.util.List;

public record UserMedicationScheduleByRangeResponse(
        List<String> notTakenMedicationDates
) {
}
