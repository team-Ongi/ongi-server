package com.solution.Ongi.domain.user.controller;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.dto.LoginRequest;
import com.solution.Ongi.domain.user.dto.SignupRequest;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;

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
         "maxMissedResponses": 3,
         "pushAgreement": true,
         "voiceAgreement": true,
         "backgroundAgreement": true
    """)
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody SignupRequest request) {
        User user = userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "발급된 토큰 이용 가능")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest request) {
        String response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/check-id")
    @Operation(summary = "아이디 중복 확인", description = "중복된 아이디가 존재하는지 확인합니다")
    public ResponseEntity<ApiResponse<String>> checkLoginIdDuplicate(@RequestParam String id) {
        String response = userService.isDuplicatedId(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }



}
