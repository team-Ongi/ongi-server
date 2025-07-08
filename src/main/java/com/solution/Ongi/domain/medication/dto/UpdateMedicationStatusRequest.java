package com.solution.Ongi.domain.medication.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateMedicationStatusRequest(
    @NotNull
    Boolean isTaken,
    String reason, // 다음에를 누른 이유
    Integer remindAfterMinutes // 다음에 누른 경우 (ex. 30, 60, null)
) { }
