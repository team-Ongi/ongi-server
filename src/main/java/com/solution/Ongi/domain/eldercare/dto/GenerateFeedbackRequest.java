package com.solution.Ongi.domain.eldercare.dto;

import jakarta.validation.constraints.NotNull;

public record GenerateFeedbackRequest(
        @NotNull
        Long todayWalkCount
) {
}
