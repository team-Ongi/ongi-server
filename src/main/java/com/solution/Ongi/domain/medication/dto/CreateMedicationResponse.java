package com.solution.Ongi.domain.medication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateMedicationResponse {
    private Long medicationId;
    private String message;
}
