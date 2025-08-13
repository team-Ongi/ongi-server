package com.solution.Ongi.infra.subscription;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.solution.Ongi.domain.push.dto.PushNotificationRequest;
import com.solution.Ongi.domain.push.service.PushNotificationService;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.infra.subscription.dto.NotificationRequest;
import com.solution.Ongi.infra.subscription.dto.SubscriptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/push-alarm/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final PushNotificationService pushNotificationService;
    private final SubscriptionService subscriptionService;

    @Operation(summary = "구독 토큰을 조회"
    )
    @PostMapping("/send")
    public ResponseEntity<String> send(
            @RequestBody NotificationRequest request) throws FirebaseMessagingException {
        String token=subscriptionService.getTokenForUser(request.userId());
        String messageId=pushNotificationService.sendToToken(
                new PushNotificationRequest(token)
        );
        return ResponseEntity.ok(messageId);
    }


    @Operation(summary = "프론트엔드에서 전달된 registrationToken과 유저를 매핑 저장")
    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(
            @RequestBody SubscriptionRequest request
    ){
        subscriptionService.save(request.userId(),request.registrationToken());
        return ResponseEntity.ok().build();
    }
}
