package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.enums.LoginMode;

public record UserInfoResponse(
    Long id,
    String loginId,
    Boolean isSenior,
    Boolean personalInfoAgreement
) {
    public static UserInfoResponse from(User user, LoginMode mode) {
        return new UserInfoResponse(
            user.getId(),
            user.getLoginId(),
            mode == LoginMode.SENIOR,
            mode == LoginMode.SENIOR ? user.getAgreement().getPersonalInfoAgreement() : null
        );
    }

}
