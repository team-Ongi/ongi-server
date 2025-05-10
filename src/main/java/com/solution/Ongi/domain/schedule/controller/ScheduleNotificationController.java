package com.solution.Ongi.domain.schedule.controller;

import com.solution.Ongi.domain.schedule.dto.UpcomingScheduleResponse;
import com.solution.Ongi.domain.schedule.service.ScheduleNotificationService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class ScheduleNotificationController {

    private final ScheduleNotificationService notificationService;

    @Operation(summary = "다음 임박 스케줄을 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<UpcomingScheduleResponse>> getNext(
            Authentication authentication
    ){
        UpcomingScheduleResponse next=notificationService.getNext(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(next));
    }

    @Operation(summary = "현재 표시된 스케줄 'true' 처리 후, 다음 스케줄 조회합니다")
    @PostMapping("/next/confirm")
    public ResponseEntity<ApiResponse<UpcomingScheduleResponse>> confirm(
            Authentication authentication
    ){
        UpcomingScheduleResponse next=notificationService.confirmAndGetNext(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(next));
    }

    @Operation(summary = "현재 표시된 스케줄 'false' 처리 후, 다음 스케줄 조회합니다",
            description= "user의 CurrentIgnoreCount 가 1 증가합니다."
    )
    @PostMapping("/next/deny")
    public ResponseEntity<ApiResponse<UpcomingScheduleResponse>> deny(
            Authentication authentication
    ){
        UpcomingScheduleResponse next=notificationService.denyAndGetNext(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(next));
    }
}
