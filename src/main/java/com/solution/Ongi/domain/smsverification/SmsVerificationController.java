package com.solution.Ongi.domain.smsverification;

import com.solution.Ongi.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsVerificationController {

    private final SmsVerificationService smsVerificationService;

    @PostMapping("/send-sms")
    public ResponseEntity<ApiResponse<String>> sendCode(@RequestParam String phoneNumber) {
        String response = smsVerificationService.sendVerificationCode(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/verify-sms")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestBody SmsVerifyRequest request) {
        boolean isValid = smsVerificationService.verifyCode(request.phoneNumber(), request.code());
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }


}
