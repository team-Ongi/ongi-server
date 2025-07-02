package com.solution.Ongi.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SmsVerifyRequest(
        @NotBlank String phoneNumber
) {
}
