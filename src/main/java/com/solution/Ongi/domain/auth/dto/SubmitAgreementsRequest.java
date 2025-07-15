package com.solution.Ongi.domain.auth.dto;

import jakarta.validation.constraints.NotNull;

public record SubmitAgreementsRequest(
        @NotNull
        Boolean agreeToPersonal,

        @NotNull
        Boolean agreeToVoice,

        @NotNull
        Boolean agreeToBackground,

        @NotNull
        Boolean agreeToAll
) {
}
