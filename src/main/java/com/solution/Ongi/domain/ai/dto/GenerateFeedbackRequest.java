package com.solution.Ongi.domain.ai.dto;

import jakarta.validation.constraints.NotNull;

public record GenerateFeedbackRequest(
        @NotNull
        Long todayWalkCount
) {
}
