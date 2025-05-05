package com.solution.Ongi.domain.medication.dto;

public record UpdateMedicationStatusResponse(
    Long scheduleId,
    Boolean isTaken,
    String message
) {

}
