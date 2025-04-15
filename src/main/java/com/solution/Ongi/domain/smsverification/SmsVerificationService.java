package com.solution.Ongi.domain.smsverification;

import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import com.solution.Ongi.global.util.SmsUtil;
import jakarta.transaction.Transactional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SmsVerificationService {

    private final SmsVerificationRepository smsVerificationRepository;
    private final SmsUtil smsUtil;

    public String sendVerificationCode(String phoneNumber) {
        smsVerificationRepository.deleteByPhoneNumber(phoneNumber);
        String code = generateRandomCode();

        // 인증번호 저장
        SmsVerification verification = SmsVerification.builder()
            .phoneNumber(phoneNumber)
            .code(code)
            .build();

        smsVerificationRepository.save(verification);

        // 인증번호 전송
        smsUtil.sendOne(phoneNumber, code);

        return "인증번호가 전송되었습니다.";
    }

    public boolean verifyCode(String phoneNumber, String inputCode) {
        SmsVerification verification = smsVerificationRepository
            .findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber)
            .orElseThrow(() -> new GeneralException(ErrorStatus.VERIFICATION_CODE_NOT_FOUND));

        if (verification.getIsVerified()) {
            throw new GeneralException(ErrorStatus.ALREADY_VERIFIED);
        }

        if (verification.getCode().equals(inputCode)){
            verification.verify();
            return true;
        }

        return false;
    }


    // 랜덤 4자리 발송
    private String generateRandomCode() {
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            numStr.append(rand.nextInt(10));
        }
        return numStr.toString();
    }

}
