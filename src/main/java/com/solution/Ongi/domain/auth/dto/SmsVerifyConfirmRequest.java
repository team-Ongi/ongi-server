package com.solution.Ongi.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SmsVerifyConfirmRequest(
        @NotBlank String phoneNumber,
        @NotBlank String code
) {
}
