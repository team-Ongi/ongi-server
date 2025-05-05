package com.solution.Ongi.domain.medication.dto;

public record UpdateMedicationStatusRequest(
    Boolean isTaken,
    String reason, // 다음에를 누른 이유
    Integer remindAfterMinutes // 다음에 누른 경우 (ex. 30, 60, null)
) {

}
