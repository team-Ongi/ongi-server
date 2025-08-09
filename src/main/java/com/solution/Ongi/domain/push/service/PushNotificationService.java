package com.solution.Ongi.domain.push;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {
    public String sendNotification(String targetToken,String title,String body) throws FirebaseMessagingException{
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
}
