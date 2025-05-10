package com.solution.Ongi.infra.firebase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name="SubscriptionRequest", description = "FCM 웹 푸시 토큰")
public record SubscriptionRequest(
        @NotNull Long userId,
        @NotBlank String registrationToken
) {}
