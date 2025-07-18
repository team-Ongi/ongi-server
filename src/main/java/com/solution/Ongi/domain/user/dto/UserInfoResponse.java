package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.enums.LoginMode;

public record UserInfoResponse(
    Long id,
    String loginId,
    String guardianName,
    String seniorName,
    Boolean isSenior,
    Boolean personalInfoAgreement
) {
    public static UserInfoResponse from(User user, LoginMode mode) {
        return new UserInfoResponse(
            user.getId(),
            user.getLoginId(),
            user.getGuardianName(),
            user.getSeniorName(),
            mode == LoginMode.SENIOR,
            mode == LoginMode.SENIOR ? user.getAgreement().getPersonalInfoAgreement() : null
        );
    }

}
