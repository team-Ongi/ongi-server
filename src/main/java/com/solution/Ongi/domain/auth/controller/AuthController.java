package com.solution.Ongi.domain.auth.controller;

import com.solution.Ongi.domain.auth.service.AuthService;
import com.solution.Ongi.domain.auth.dto.LoginRequest;
import com.solution.Ongi.domain.auth.dto.LoginResponse;
import com.solution.Ongi.domain.auth.dto.SignupRequest;
import com.solution.Ongi.domain.auth.dto.SignupResponse;
import com.solution.Ongi.global.response.ApiResponse;
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
@Tag(name = "인증")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = """
        "loginId": "hihi123",
         "password": "1234",
        "guardianName": "김보호",
        "guardianPhoneNumber": "01012345678",
        "seniorName": "이어르신",
        "seniorAge": 80,
        "seniorPhone": "01056781234",
         "relation": "SON",
         "alertMax": "MINUTES_30"
         "ignoreCnt": 3,
         "pushAgreement": true,
         "voiceAgreement": true,
         "backgroundAgreement": true
    """)
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
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/id/duplicate")
    @Operation(summary = "아이디 중복 확인", description = "중복된 아이디가 존재하는지 확인합니다")
    public ResponseEntity<ApiResponse<String>> checkLoginIdDuplicate(@RequestParam String id) {
        String response = authService.isDuplicatedId(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/token/reissue")
    @Operation(summary = "accessToken 재발급", description = "refreshToken을 헤더에 담아 전송하면 새로운 accessToken을 발급합니다.")
    public ResponseEntity<ApiResponse<String>> reissue(@Parameter(hidden = true) @RequestHeader("Authorization") String refreshToken) {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        String newAccessToken = authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(newAccessToken));
    }
}
