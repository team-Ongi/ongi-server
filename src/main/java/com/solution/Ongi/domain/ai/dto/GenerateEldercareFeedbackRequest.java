package com.solution.Ongi.domain.ai.dto;

import jakarta.validation.constraints.NotNull;

public record GenerateEldercareFeedbackRequest(
        @NotNull
        Long user_id,

        @NotNull
        Float lat,

        @NotNull
        Float lon,

        @NotNull
        Long today_walk_count
) {
}
