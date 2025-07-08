package com.solution.Ongi.domain.medication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateFixedTimeMedicationRequest(
    @NotBlank
    String medicationName,

    @NotBlank
    String time,

    @NotNull
    Long medicationScheduleId
) {
}
