package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.user.enums.LoginMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotNull @NotEmpty
    String id,
    @NotNull @NotEmpty
    String password,
    @NotNull @NotEmpty
    LoginMode mode
) {

}
