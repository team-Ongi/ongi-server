package com.solution.Ongi.domain.schedule.controller;

import com.solution.Ongi.domain.schedule.dto.DenyRequest;
import com.solution.Ongi.domain.schedule.dto.UpcomingScheduleResponse;
import com.solution.Ongi.domain.schedule.service.ScheduleNotificationService;
import com.solution.Ongi.global.response.ApiResponse;
import com.solution.Ongi.global.response.code.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class ScheduleNotificationController {

    private final ScheduleNotificationService notificationService;

    @Operation(
            summary = "다음 임박 스케줄 조회",
            description = """
            현재 사용자 기준으로 다음에 처리할 하나의 '임박 스케줄'을 반환합니다.
            오늘 남은 스케줄이 없으면 null을 반환합니다.
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다음 임박 스케줄 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpcomingScheduleResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "임박한 스케줄 없음")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값이 잘못된 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스케줄 ID가 존재하지 않는 경우", content = @Content)
    @GetMapping
    public ResponseEntity<ApiResponse<UpcomingScheduleResponse>> getNext(
            Authentication authentication
    ){
        UpcomingScheduleResponse next=notificationService.getNext(authentication.getName());
        if (next== null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ApiResponse.success(next, SuccessStatus.SUCCESS_200));
    }

    @Operation(
            summary = "현재 스케줄 완료 처리 후 다음 스케줄 조회",
            description = "현재 표시된 스케줄을 완료(true) 처리하고 즉시 다음 임박 스케줄을 반환합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "확인 처리 및 다음 스케줄 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpcomingScheduleResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "임박한 스케줄 없음")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값이 잘못된 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스케줄 ID가 존재하지 않는 경우", content = @Content)
    @PostMapping("/next/confirm")
    public ResponseEntity<ApiResponse<UpcomingScheduleResponse>> confirm(
            Authentication authentication
    ){
        UpcomingScheduleResponse next=notificationService.confirmAndGetNext(authentication.getName());
        return (next==null)
            ? ResponseEntity.ok(ApiResponse.success(null,SuccessStatus.SUCCESS_204))
            : ResponseEntity.ok(ApiResponse.success(next,SuccessStatus.SUCCESS_200));
    }

    @Operation(
            summary = "현재 스케줄 거부 처리 후 다음 스케줄 조회",
            description = """
            현재 표시된 스케줄을 거부(false) 처리하고 다음 임박 스케줄을 반환합니다.
            사용자 CurrentIgnoreCount는 증가하지 않습니다.
            """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "거부 사유를 담은 요청 바디",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DenyRequest.class),
                    examples = @ExampleObject(
                            name = "거부 요청 예시",
                            value = """
                {
                  "reason": "배가 안 고픔"
                }
                """
                    )
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "거부 처리 및 다음 스케줄 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpcomingScheduleResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "임박한 스케줄 없음")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 경우", content = @Content)
    @PostMapping("/next/deny")
    public ResponseEntity<ApiResponse<UpcomingScheduleResponse>> deny(
            Authentication authentication,
            @RequestBody DenyRequest request
            ){
        UpcomingScheduleResponse next=notificationService.denyAndGetNext(authentication.getName(),request);
        return (next==null)
                ? ResponseEntity.ok(ApiResponse.success(null,SuccessStatus.SUCCESS_204))
                : ResponseEntity.ok(ApiResponse.success(next,SuccessStatus.SUCCESS_200));    }
}
