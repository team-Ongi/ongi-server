package com.solution.Ongi.domain.push.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PushNotificationService {
    public String sendNotification(String targetToken,String title,String body)
            throws FirebaseMessagingException{
        Notification notification= Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        Message message=Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .build();
        return FirebaseMessaging.getInstance().send(message);
    }

    // 여러 기기 동시 전송
    // 해당 계정 이용자에게 모두 전송
    public List<String> sendToTokens(List<String> tokens, String title, String body)
        throws FirebaseMessagingException {
        if (tokens == null || tokens.isEmpty()) return List.of();

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // FCM 멀티캐스트 1회 호출 시 최대 토큰 수 (청킹 크기)
        final int MAX = 500;
        List<String> invalid = new ArrayList<>();

        for (int start = 0; start < tokens.size(); start += MAX) {
            // 500개로 제한한 sublist
            List<String> chunk = tokens.subList(start, Math.min(start + MAX, tokens.size()));

            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .addAllTokens(chunk)
                    .build();

            BatchResponse batchResponse=FirebaseMessaging.getInstance().sendEachForMulticast(message);

            //실패 토큰 수집
            List<SendResponse> responses=batchResponse.getResponses();
            for(int i=0; i< responses.size(); i++){
                SendResponse r= responses.get(i);
                if(!r.isSuccessful()){
                    MessagingErrorCode code=r.getException().getMessagingErrorCode();
                    // register 되지 않은 토큰, 인증되지 않은 사용자 토큰 비활성화
                    if (code==MessagingErrorCode.UNREGISTERED || code==MessagingErrorCode.INVALID_ARGUMENT){
                        invalid.add(chunk.get(i));
                    }
                }
            }
        }
        return invalid;
    }
}
