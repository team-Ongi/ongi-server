package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.user.enums.LoginMode;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    LoginMode mode
) {

}
