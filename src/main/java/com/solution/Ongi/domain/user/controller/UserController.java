package com.solution.Ongi.domain.user.controller;

import com.solution.Ongi.domain.user.dto.LoginRequest;
import com.solution.Ongi.domain.user.dto.LoginResponse;
import com.solution.Ongi.domain.user.dto.SignupRequest;
import com.solution.Ongi.domain.user.dto.SignupResponse;
import com.solution.Ongi.domain.user.dto.UserInfoResponse;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.jwt.JwtTokenProvider;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

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
        SignupResponse response = userService.signup(request);
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
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/check-id")
    @Operation(summary = "아이디 중복 확인", description = "중복된 아이디가 존재하는지 확인합니다")
    public ResponseEntity<ApiResponse<String>> checkLoginIdDuplicate(@RequestParam String id) {
        String response = userService.isDuplicatedId(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/reissue")
    @Operation(summary = "accessToken 재발급", description = "refreshToken을 헤더에 담아 전송하면 새로운 accessToken을 발급합니다.")
    public ResponseEntity<ApiResponse<String>> reissue(@Parameter(hidden = true) @RequestHeader("Authorization") String refreshToken) {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        String newAccessToken = userService.reissue(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(newAccessToken));
    }

    @GetMapping("/me")
    @Operation(
        summary = "유저 정보 조회",
        description = """
        현재 로그인한 사용자의 정보를 반환합니다. <br><br>
        - 보호자 모드로 로그인할 시 개인정보 동의 여부(`personalInfoAgreement`) null로 반환,
         노약자 모드로 로그인 시 개인정보 동의 여부(`personalInfoAgreement`) boolean값으로 반환<br>
        """
    )
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyProfile(
        Authentication authentication,
        HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = authentication.getName();

        UserInfoResponse response = userService.getUserInfoWithMode(token, loginId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/guardian-agreement")
    @Operation(summary = "보호자 최초 로그인시 개인정보 약관 동의")
    public ResponseEntity<ApiResponse<String>> agreeGuardianTerms(Authentication authentication) {
        userService.markGuardianAgreement(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("약관에 동의하였습니다."));
    }

}
