package com.solution.Ongi.domain.auth.dto;


public record SmsVerifyRequest(
    String phoneNumber,
    String code
) {

}
