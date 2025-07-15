package com.solution.Ongi.global.util;

import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String apiKey;
    @Value("${coolsms.api.secret}")
    private String apiSecretKey;
    @Value("${coolsms.api.number}")
    private String sendNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
    }

    // 단일 메시지 발송 예제
    public SingleMessageSentResponse sendOne(String to, String verificationCode) {
        Message message = new Message();
        message.setFrom(sendNumber);
        message.setTo(to);
        message.setText("[ONGI] 아래의 인증번호를 입력해주세요\n" + verificationCode);

        try {
            return this.messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            // 외부 서비스에서 정의한 커스텀 예외
            log.error("메시지 전송 실패 - 사유: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}