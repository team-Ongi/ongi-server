package com.solution.Ongi.domain.user.controller;

import com.solution.Ongi.domain.user.dto.UserInfoResponse;
import com.solution.Ongi.domain.user.dto.UserVoiceResponse;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;

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

    @DeleteMapping("")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 시 사용하는 URI로 보호자와 어르신의 정보가 모두 삭제됩니다.")
    public ResponseEntity<ApiResponse<String>> deleteGuardianTerms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService.deleteUser(authentication.getPrincipal().toString());
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴 완료"));
    }

    @PostMapping(path = "/voice/record", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "보호자 목소리 녹음하기",
            description = "보호자가 목소리를 녹음할 때 사용하는 API입니다"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "음성 녹음 성공", content = @Content(mediaType = "application/json",schema =@Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "음성 녹음 실패", content = @Content(mediaType = "application/json",schema =@Schema()))
    public ResponseEntity<ApiResponse<String>> recordVoice(@RequestParam("file") MultipartFile file){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService.recordVoice(file, authentication.getPrincipal().toString());
        return ResponseEntity.ok(ApiResponse.success("음성 녹음 완료"));
    }

    @GetMapping(path = "/voice")
    @Operation(
            summary = "목소리 녹음 조회",
            description = "보호자가 목소리를 녹음한 경우에는 보호자 목소리 녹음 파일 경로를 반환하고, 녹음하지 않은 경우에는 기본 아나운서 목소리 녹음 파일 경로를 반환합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음성 녹음 파일 조회 성공", content = @Content(mediaType = "application/json",schema =@Schema(implementation = UserVoiceResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
    public ResponseEntity<ApiResponse<UserVoiceResponse>> getUserVoice(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserVoiceResponse response = userService.getUserVoice(authentication.getPrincipal().toString());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
