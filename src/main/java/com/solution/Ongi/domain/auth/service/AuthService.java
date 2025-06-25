package com.solution.Ongi.domain.auth.service;

import com.solution.Ongi.domain.agreement.Agreement;
import com.solution.Ongi.domain.auth.dto.*;
import com.solution.Ongi.domain.smsverification.SmsVerification;
import com.solution.Ongi.domain.smsverification.SmsVerificationRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.global.jwt.JwtTokenProvider;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SmsVerificationRepository smsVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;

    // 회원가입
    public SignupResponse signup(SignupRequest request) {

        // 로그인 아이디 유효 검사
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
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

        return new LoginResponse(accessToken, refreshToken, request.mode());
    }

    // 로그인 아이디 중복 체크
    public boolean isDuplicatedId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    // Access Token 재발급
    public String reissueAccessToken(String refreshToken) {
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

        return jwtProvider.createToken(user.getLoginId(), mode);
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

}

