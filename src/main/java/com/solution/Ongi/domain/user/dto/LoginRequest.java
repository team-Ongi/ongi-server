package com.solution.Ongi.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotNull @NotEmpty
    String id,
    @NotNull @NotEmpty
    String password
) {

}
