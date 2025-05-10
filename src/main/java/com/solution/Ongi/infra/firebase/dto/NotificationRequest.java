package com.solution.Ongi.infra.firebase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name="NotificationRequest", description = "푸시 알림 전송 요청 데이터입니다.")
public record NotificationRequest(
        @NotNull @Schema(description="사용자 ID", example="1") Long userId,
        @NotBlank @Schema(description="제목", example="긴급 상황") String title,
        @NotBlank @Schema(description="메시지", example="현재 누락 횟수가 최대치에 도달했습니다.") String body
) { }
