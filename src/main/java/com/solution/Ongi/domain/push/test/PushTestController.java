package com.solution.Ongi.domain.push.test;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.solution.Ongi.domain.push.service.DeviceTokenService;
import com.solution.Ongi.domain.push.test.PushTestRequest;
import com.solution.Ongi.domain.push.test.PushTestService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/push/test")
public class PushTestController {

    private final PushTestService pushTestService;
    private final DeviceTokenService deviceTokenService; // 유저의 활성 토큰 조회용

    @Operation(summary = "단일 토큰 푸시 알림 테스트")
    @PostMapping("/token")
    public ResponseEntity<?> sendToToken(@RequestBody PushTestRequest req) {
        try {
            if (req.token() == null || req.token().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "token이 비어있습니다."));
            }
            String id = pushTestService.sendToToken(req);
            return ResponseEntity.ok(Map.of("messageId", id));
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", e.getMessage()));
        }
    }
    
}
