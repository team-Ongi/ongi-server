package com.solution.Ongi.domain.schedule.service;


import com.solution.Ongi.domain.push.PushNotificationService;
import com.solution.Ongi.infra.firebase.SubscriptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SubscriptionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SubscriptionService subscriptionService;

    @MockBean
    PushNotificationService pushNotificationService;

    @DisplayName("구독 정보 저장 API 호출 시 200 OK")
    @Test
    void subscribe_endpoint_shouldReturn200() throws Exception {
        // given
        String body = """
            {
              "userId": 1,
              "registrationToken": "dummy-token-123"
            }
            """;

        // when / then
        mockMvc.perform(post("/api/push-alarm/subscription/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(subscriptionService).save(1L, "dummy-token-123");
    }

    @DisplayName("푸시 전송 API 호출 시 messageId 리턴")
    @Test
    void send_endpoint_shouldReturnMessageId() throws Exception {
        // given
        when(subscriptionService.getTokenForUser(1L)).thenReturn("dummy-token-123");
        when(pushNotificationService.sendNotification(
                eq("dummy-token-123"), anyString(), anyString()))
                .thenReturn("projects/ongi/messages/abc123");

        String body = """
            {
              "userId": 1,
              "title": "테스트 제목",
              "body": "테스트 내용"
            }
            """;

        // when / then
        mockMvc.perform(post("/api/push-alarm/subscription/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("projects/ongi/messages/abc123"));
    }
}