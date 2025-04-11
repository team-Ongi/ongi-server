package com.solution.Ongi.domain.user.controller;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.dto.LoginRequest;
import com.solution.Ongi.domain.user.dto.SignupRequest;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        "guardianPhoneNumber": "010-1234-5678",
        "seniorName": "이어르신",
        "seniorAge": 80,
        "seniorPhone": "010-5678-1234",
         "relation": "SON",
         "alertMax": "MINUTES_30"
    """)
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody SignupRequest request) {
        User user = userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest request) {
        String response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }



}
