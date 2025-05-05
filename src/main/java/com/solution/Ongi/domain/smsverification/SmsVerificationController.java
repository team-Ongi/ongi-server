package com.solution.Ongi.domain.smsverification;

import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "문자 인증번호 보내기", description = "해당 전화번호에 인증메시지를 보냅니다.")
    public ResponseEntity<ApiResponse<String>> sendCode(@RequestParam String phoneNumber) {
        String response = smsVerificationService.sendVerificationCode(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/verify-sms")
    @Operation(summary = "인증번호 확인", description = "해당 전화번호의 인증번호를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestBody SmsVerifyRequest request) {
        boolean isValid = smsVerificationService.verifyCode(request.phoneNumber(), request.code());
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }


}
