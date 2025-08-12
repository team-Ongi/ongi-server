package com.solution.Ongi.domain.push.test;

import com.google.firebase.messaging.*;
import com.solution.Ongi.domain.push.service.DeviceTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class PushTestService {
    private ApnsConfig apnsAlert() {
        return ApnsConfig.builder()
                .putHeader("apns-push-type", "alert") // iOS 표시형
                .putHeader("apns-priority", "10")     // 즉시 표시
                .setAps(Aps.builder()
                        .setSound("default")          // 번들 사운드 or "default"// 필요시
                        .build())
                .build();
    }

    public String sendToToken(PushTestRequest req) throws FirebaseMessagingException {

        log.info("[FCM][token] try title='{}', bodyLen={}, tokenTail={}",
                req.title(), req.body() == null ? 0 : req.body().length(),
                req.token().substring(Math.max(0, req.token().length() - 8)));

        Message message = base(req).setToken(req.token()).build();
        String id = FirebaseMessaging.getInstance().send(message);

        log.info("[FCM][token] sent messageId={}", id);
        return id;
    }

    public BatchResponse sendToTokens(PushTestRequest request, List<String> tokens)
            throws FirebaseMessagingException {

        MulticastMessage message = MulticastMessage.builder()
                .setApnsConfig(apnsAlert())            // ← 여기도 alert 세팅
                .setNotification(notification(request))
                .addAllTokens(tokens)
                .build();

        BatchResponse resp = FirebaseMessaging.getInstance().sendEachForMulticast(message);

        // 무효 토큰 정리 예시 (DB 정리까지는 고도화 단계에서)
        for (int i = 0; i < resp.getResponses().size(); i++) {
            SendResponse r = resp.getResponses().get(i);
            if (!r.isSuccessful()) {
                MessagingErrorCode code = r.getException().getMessagingErrorCode();
                if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT) {
                    String invalid = tokens.get(i);
                    log.warn("[FCM] invalid token detected: {}", invalid);
                    // TODO: DeviceTokenService 등으로 비활성화 처리
                }
            }
        }
        return resp;
    }

    // 메시지 빌더
    private Message.Builder base(PushTestRequest req) {
        return Message.builder()
                .setApnsConfig(apnsAlert())
                .setNotification(notification(req));
    }

    private Notification notification(PushTestRequest request){
        return Notification.builder()
                .setTitle(request.title() != null ? request.title() : "FCM 테스트")
                .setBody(request.body() != null ? request.body() : "FCM body입니다")
                .build();
    }
}


