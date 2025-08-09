package com.solution.Ongi.domain.schedule.dto;

import jakarta.validation.constraints.NotBlank;

public record DenyRequest(
        @NotBlank
        String reason
) {
}
