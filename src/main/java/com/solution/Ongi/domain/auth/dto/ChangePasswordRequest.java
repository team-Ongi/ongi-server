package com.solution.Ongi.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank
        String id,

        @NotBlank
        String password
) {
}
