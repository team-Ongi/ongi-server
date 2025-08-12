package com.solution.Ongi.domain.ai.dto;

import com.solution.Ongi.domain.medication.dto.MedicationInfoFromFastAPIResponse;

public record GenerateMedicationToSqsRequest(
        String loginId,
        MedicationInfoFromFastAPIResponse medicationData
) {
}
