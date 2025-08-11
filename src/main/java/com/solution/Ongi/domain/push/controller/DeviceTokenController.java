package com.solution.Ongi.domain.push.controller;

import com.solution.Ongi.domain.push.dto.DeactivateDeviceRequest;
import com.solution.Ongi.domain.push.dto.RegisterDeviceRequest;
import com.solution.Ongi.domain.push.service.DeviceTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/devices")
public class DeviceTokenController {
    private final DeviceTokenService deviceTokenService;

    @Operation(
            summary = "디바이스 토큰 등록/갱신",
            description = """
            로그인 직후 / FCM 토큰이 갱신될 때 호출하여 이용합니다.
            동일한 토큰이 존재하면 현재 계정으로 소유자를 재바인딩하고 활성화 상태로 갱신합니다.
            """,
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterDeviceRequest.class),
                            examples = @ExampleObject(name = "register-example",
                                    value = """
                           {
                             "token": "fcm_token",
                             "platform": "ANDROID",
                             "deviceId": "SM-G99"
                           }
                           """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "등록/갱신 성공"),
                    @ApiResponse(responseCode = "400", description = "요청 바디가 올바르지 않음"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않음")
            }
    )
    //TODO: Exception 공통 처리
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            Authentication auth,
            @RequestBody RegisterDeviceRequest request){
        deviceTokenService.register(auth.getName(), request.token(), request.platform(), request.deviceId());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "디바이스 토큰 비활성화",
            description = "로그아웃 등으로 더 이상 사용하지 않는 FCM 토큰을 비활성화합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DeactivateDeviceRequest.class),
                            examples = @ExampleObject(name = "deactivate-example",
                                    value = """
                           {
                             "token": "fcm_token"
                           }
                           """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "비활성화 성공"),
                    @ApiResponse(responseCode = "400", description = "요청 바디가 올바르지 않음")
            }
    )
    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivate(
            @RequestBody DeactivateDeviceRequest request){
        deviceTokenService.deactivateByToken(request.token());
        return ResponseEntity.ok().build();
    }
}
