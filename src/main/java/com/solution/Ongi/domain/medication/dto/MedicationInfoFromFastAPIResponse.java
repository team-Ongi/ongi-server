package com.solution.Ongi.domain.medication.dto;

import java.util.List;

public record MedicationInfoFromFastAPIResponse(
       List<CreateMealBasedMedicationFromFastAPIRequest> medicationInfoFromFastAPIResponse
) {
}
