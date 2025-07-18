package com.solution.Ongi.domain.auth.service;

import com.solution.Ongi.domain.agreement.Agreement;
import com.solution.Ongi.domain.auth.dto.*;
import com.solution.Ongi.domain.smsverification.SmsVerification;
import com.solution.Ongi.domain.smsverification.SmsVerificationRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.domain.user.repository.UsersMealVoiceRepository;
import com.solution.Ongi.domain.user.repository.UsersMedicationVoiceRepository;
import com.solution.Ongi.global.jwt.JwtTokenProvider;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import com.solution.Ongi.global.util.SmsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SmsVerificationRepository smsVerificationRepository;
    private final SmsUtil smsUtil;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;
    private final UsersMedicationVoiceRepository usersMedicationVoiceRepository;
    private final UsersMealVoiceRepository usersMealVoiceRepository;

    // 회원가입
    public SignupResponse signup(SignupRequest request) {

        // 회원가입 아이디 존재 여부 검사
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new GeneralException(ErrorStatus.ALREADY_EXIST_USER);
        }

        // 인증요청 한 전화번호인지 검사
        SmsVerification verification = smsVerificationRepository
                .findTopByPhoneNumberOrderByCreatedAtDesc(request.guardianPhone())
                .orElseThrow(() -> new GeneralException(ErrorStatus.VERIFICATION_CODE_NOT_FOUND));

        // 인증된 전화번호인지 검사
        if (!verification.getIsVerified()) {
            throw new GeneralException(ErrorStatus.VERIFICATION_NOT_COMPLETED);
        }

        // Agreement 객체 생성
        Agreement agreement = Agreement.builder()
                .pushAgreement(request.pushAgreement())
                .voiceAgreement(request.voiceAgreement())
                .backgroundAgreement(request.backgroundAgreement())
                .build();

        // 유저 객체 생성
        User user = User.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .guardianName(request.guardianName())
                .guardianPhone(request.guardianPhone())
                .seniorName(request.seniorName())
                .seniorAge(request.seniorAge())
                .seniorPhone(request.seniorPhone())
                .relation(request.relation())
                .alertMax(request.alertMax())
                .ignoreCnt(request.ignoreCnt())
                .ignoreCnt(0)
                .agreement(agreement)
                .currentIgnoreCnt(0)
                .build();
        userRepository.save(user);

        // 인증완료한 전화번호 삭제
        smsVerificationRepository.deleteByPhoneNumber(request.guardianPhone());
        return new SignupResponse(user.getId(), user.getLoginId());
    }

    // 로그인
    public LoginResponse login(LoginRequest request) {

        // 유저가 존재하는지 검사
        User user = userRepository.findByLoginId(request.id())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 비밀번호 검사
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String accessToken = jwtProvider.createToken(user.getLoginId(), request.mode().name());
        String refreshToken = jwtProvider.createRefreshToken(user.getLoginId());

        // 유저 정보에 RefreshToken 추가
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // 유저의 녹음 파일 리스트 반환
        List<String> usersMealVoiceList = usersMealVoiceRepository.findVoiceFileUrlByUserId(user.getId());
        List<String> usersMedicationVoiceList = usersMedicationVoiceRepository.findVoiceFileUrlByUserId(user.getId());

        return new LoginResponse(accessToken, refreshToken, request.mode(),usersMealVoiceList, usersMedicationVoiceList,user.getIsServiceAgreed(),user.getGuardianName(),user.getSeniorName());
    }

    // 로그인 후 정보 제공 동의 체크
    public void submitAgreement(String loginId, SubmitAgreementsRequest request){
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        if(!request.agreeToAll())
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        if(!request.agreeToBackground() || !request.agreeToVoice() || !request.agreeToPersonal())
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        user.updateIsServiceAgreed(true);
        userRepository.save(user);
    }

    // 로그인 아이디 중복 체크
    public CheckLoginIdDuplicateResponse isDuplicatedId(String loginId) {
        return new CheckLoginIdDuplicateResponse(userRepository.existsByLoginId(loginId));
    }

    // Access Token 재발급
    public ReissueAccessTokenResponse reissueAccessToken(String refreshToken) {
        if (!jwtProvider.isValidToken(refreshToken)) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        }

        String loginId = jwtProvider.getLoginIdFromToken(refreshToken);
        String mode = jwtProvider.getModeFromToken(refreshToken);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new GeneralException(ErrorStatus.TOKEN_MISMATCH);
        }

        return new ReissueAccessTokenResponse(jwtProvider.createToken(user.getLoginId(), mode));
    }

    public FindLoginIdResponse findLoginId(String phoneNumber){
        User user = userRepository.findBySeniorPhoneOrGuardianPhone(phoneNumber,phoneNumber).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return new FindLoginIdResponse(user.getLoginId());
    }

    // 비밀번호 변경
    public String changePassword(ChangePasswordRequest request) {
        // 유저 정보 불러오기
        User user = userRepository.findByLoginId(request.id())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 동일한 비밀번호인지 확인
        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.SAME_AS_OLD_PASSWORD);
        }

        // 새 비밀번호 암호화 및 저장
        String encodedPassword = passwordEncoder.encode(request.password());
        user.updatePassword(encodedPassword);
        userRepository.save(user);

        return "비밀번호 변경 완료";
    }

    // 인증번호 발송
    public String sendVerificationCode(String phoneNumber) {
        // 기존 정보 삭제 및 인증번호 생성
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

    // 인증번호 확인
    public SmsVerifyConfirmResponse verifyCode(String phoneNumber, String inputCode) {
        // 인증요청을 한 전화번호인지 확인
        SmsVerification verification = smsVerificationRepository
                .findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VERIFICATION_CODE_NOT_FOUND));

        // 인증번호 유효시간 검사
        long seconds = ChronoUnit.SECONDS.between(verification.getCreatedAt(), LocalDateTime.now());
        if (seconds >= 300 && !verification.getIsVerified())
            throw new GeneralException(ErrorStatus.VERIFICATION_EXPIRED);

        if (!(verification.getCode().equals(inputCode))){
            throw new GeneralException(ErrorStatus.VERIFICATION_CODE_MISMATCH);
        }

        // 인증번호가 같다면 -> is_verified true로 변경
        verification.verify();
        return new SmsVerifyConfirmResponse(true);
    }

    // 랜덤 4자리 생성
    private String generateRandomCode() {
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            numStr.append(rand.nextInt(10));
        }
        return numStr.toString();
    }
}

