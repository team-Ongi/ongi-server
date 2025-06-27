package com.solution.Ongi.domain.user.controller;

import com.solution.Ongi.domain.user.dto.UserInfoResponse;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.jwt.JwtTokenProvider;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

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
    @Operation(
        summary = "보호자 최초 로그인시 개인정보 약관 동의",
        description = """
        보호자 최초 로그인 시 개인정보 수집 및 이용 동의가 필요합니다. <br><br>
        - 해당 API는 보호자 로그인 직후 최초 1회 호출되어야 하며,<br>
        - 호출 시 해당 보호자의 `personalInfoAgreement` 값이 true로 업데이트됩니다.<br>
        - 이후 이 값은 `/user/me` API에서 확인할 수 있습니다.
        """
    )
    public ResponseEntity<ApiResponse<String>> agreeGuardianTerms(Authentication authentication) {
        userService.markGuardianAgreement(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("약관에 동의하였습니다."));
    }

}
