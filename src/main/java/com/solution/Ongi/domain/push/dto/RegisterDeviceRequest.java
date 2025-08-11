package com.solution.Ongi.domain.push.dto;

public record RegisterDeviceRequest(
        String token,
        String platform,
        String deviceId
) {
}
