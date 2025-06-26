package com.solution.Ongi.domain.medication.dto;

public record UpdateFixedTimeMedicationRequest(
    String medicationName,
    String time,
    Long medicationScheduleId
) {

}
