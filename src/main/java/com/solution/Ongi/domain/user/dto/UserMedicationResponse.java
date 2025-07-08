package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.medication.dto.MedicationResponse;

import java.util.List;

public record UserMedicationResponse(
        List<MedicationResponse> userMedicationList
) {
}
