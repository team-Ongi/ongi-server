package com.solution.Ongi.domain.medication.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateMedicationStatusRequest(
    @NotNull
    Boolean isTaken,
    String  notTakenReason// 다음에를 누른 이유
) { }
