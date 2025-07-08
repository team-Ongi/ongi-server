package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.medication.dto.MedicationScheduleResponse;

import java.util.List;

public record UserMedicationScheduleResponse(
        List<MedicationScheduleResponse> userMedicationScheduleList
) {
}
