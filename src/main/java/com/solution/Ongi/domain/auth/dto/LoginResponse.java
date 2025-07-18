package com.solution.Ongi.domain.auth.dto;

import com.solution.Ongi.domain.user.enums.LoginMode;

import java.util.List;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    LoginMode mode,
    List<String> mealVoiceList,
    List<String> medicationVoiceList,
    Boolean isServiceAgreed,
    String guardianName,
    String seniorName
) {

}
