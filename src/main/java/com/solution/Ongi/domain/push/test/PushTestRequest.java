package com.solution.Ongi.domain.push.test;

public record PushTestRequest(
        String token,
        String title,
        String body
) {
}
