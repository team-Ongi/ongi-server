package com.solution.Ongi.domain.auth.controller;

import com.solution.Ongi.domain.auth.dto.*;
import com.solution.Ongi.domain.auth.service.AuthService;
import com.solution.Ongi.domain.auth.dto.SmsVerifyRequest;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "auth-controller")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = """
        "loginId":"pooreum",
        "password":"pooreum02!",
        "guardianName":"정푸름",
        "guardianPhoneNumber":"01052882669",
        "seniorName":"온기",
        "seniorAge":80,
        "seniorPhone":"01012345678",
        "relation":"SON",
        "alertMax":"MINUTES_30"
        "ignoreCnt":3,
        "pushAgreement":true,
        "voiceAgreement":true,
        "backgroundAgreement":true
    """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(mediaType = "application/json",schema =@Schema(implementation = SignupResponse.class, type = "application/json")))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증이 완료되지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않는 경우 or 인증 요청을 하지 않은 전화번호인 경우", content = @Content)
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = """
        로그인 시 보호자/어르신 모드를 선택해야 합니다. ("GUARDIAN" 또는 "SENIOR")
        - 로그인 성공 시 accessToken과 refreshToken이 발급되며,
          accessToken은 Authorization 헤더에 담아 API 호출 시 사용합니다.
        - accessToken 유효기간은 1시간입니다.
    """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "로그인 성공", content = @Content(mediaType = "application/json",schema =@Schema(implementation = LoginResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않는 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "아이디가 존재하지 않는 경우", content = @Content)
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/id/duplicate")
    @Operation(summary = "아이디 중복 확인", description = "중복된 아이디가 존재하는지 확인합니다")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "아이디 중복 확인 완료", content = @Content(mediaType = "application/json",schema = @Schema(implementation = CheckLoginIdDuplicateResponse.class)))
    public ResponseEntity<ApiResponse<CheckLoginIdDuplicateResponse>> checkLoginIdDuplicate(@RequestParam String id) {
        CheckLoginIdDuplicateResponse response = authService.isDuplicatedId(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/token/reissue")
    @Operation(summary = "accessToken 재발급", description = "refreshToken을 헤더에 담아 전송하면 새로운 accessToken을 발급합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Access Token 재발급 성공", content = @Content( mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰이 유효하지 않거나 일치하지 않는 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않는 경우", content = @Content)
    public ResponseEntity<ApiResponse<String>> reissueAccessToken(@Parameter(hidden = true) @RequestHeader("Authorization") String refreshToken) {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        String newAccessToken = authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(newAccessToken));
    }

    @GetMapping("/find-id")
    @Operation(summary = "아이디 조회", description = "전화번호를 통해 loginId를 조회합니다")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "아이디 조회 완료", content = @Content( mediaType = "application/json",schema =@Schema(implementation = FindLoginIdResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "아이디가 존재하지 않는 경우", content = @Content)
    public ResponseEntity<ApiResponse<FindLoginIdResponse>> findLoginId(@RequestParam String phoneNumber) {
        FindLoginIdResponse response = authService.findLoginId(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "비밀번호 변경 완료", content = @Content( mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저(ID)가 존재하지 않는 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "기존 비밀번호와 같은 비밀번호를 입력한 경우", content = @Content)
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        String response = authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/verify-phone")
    @Operation(summary = "문자 인증번호 보내기", description = "해당 전화번호에 인증메시지를 보냅니다.")
    public ResponseEntity<ApiResponse<String>> sendCode(@RequestParam String phoneNumber) {
        String response = authService.sendVerificationCode(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/verify-phone/confirm")
    @Operation(summary = "인증번호 확인", description = "해당 전화번호의 인증번호를 확인합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "인증번호 확인 완료", content = @Content(mediaType = "application/json", schema =@Schema(implementation = VerifyPhoneConfirmResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증번호 시간이 만료되었거나 인증코드가 일치하지 않는 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 전화번호로 인증번호 발송 요청을 한 적이 없는 경우", content = @Content)
    public ResponseEntity<ApiResponse<VerifyPhoneConfirmResponse>> verifyCode(@RequestBody SmsVerifyRequest request) {
        VerifyPhoneConfirmResponse response = authService.verifyCode(request.phoneNumber(), request.code());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
