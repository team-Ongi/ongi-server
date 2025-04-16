package com.solution.Ongi.domain.smsverification;


public record SmsVerifyRequest(
    String phoneNumber,
    String code
) {

}
