package com.solution.Ongi.domain.user.service;

import com.solution.Ongi.domain.agreement.Agreement;
import com.solution.Ongi.domain.smsverification.SmsVerification;
import com.solution.Ongi.domain.smsverification.SmsVerificationRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.dto.LoginRequest;
import com.solution.Ongi.domain.user.dto.LoginResponse;
import com.solution.Ongi.domain.user.dto.SignupRequest;
import com.solution.Ongi.domain.user.dto.SignupResponse;
import com.solution.Ongi.domain.user.dto.UserInfoResponse;
import com.solution.Ongi.domain.user.enums.LoginMode;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.global.jwt.JwtTokenProvider;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final SmsVerificationRepository smsVerificationRepository;
    private final JwtTokenProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }

        SmsVerification verification = smsVerificationRepository
            .findTopByPhoneNumberOrderByCreatedAtDesc(request.guardianPhone())
            .orElseThrow(() -> new GeneralException(ErrorStatus.VERIFICATION_CODE_NOT_FOUND));

        if (!verification.getIsVerified()) {
            throw new GeneralException(ErrorStatus.VERIFICATION_NOT_COMPLETED);
        }

        Agreement agreement = Agreement.builder()
            .pushAgreement(request.pushAgreement())
            .voiceAgreement(request.voiceAgreement())
            .backgroundAgreement(request.backgroundAgreement())
            .build();

        User user = User.builder()
            .loginId(request.loginId())
            .password(request.password())
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

        user.encodePassword(passwordEncoder);

        userRepository.save(user);

        return new SignupResponse(user.getId(), user.getLoginId());

    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.id())
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createToken(user.getLoginId(), request.mode().name());
        String refreshToken = jwtProvider.createRefreshToken(user.getLoginId());

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponse(accessToken, refreshToken, request.mode());

    }

    public String reissue(String refreshToken) {
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

    public UserInfoResponse getUserInfoWithMode(String token, String loginId) {
        LoginMode mode = LoginMode.valueOf(jwtProvider.getModeFromToken(token));
        User user = getUserByLoginIdOrThrow(loginId);
        return UserInfoResponse.from(user, mode);
    }

    public void markGuardianAgreement(String loginId) {
        User user = getUserByLoginIdOrThrow(loginId);
        user.getAgreement().markPersonalInfoAgreement();
    }

    public String isDuplicatedId(String loginId) {
        boolean exists = userRepository.existsByLoginId(loginId);
        return exists ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.";
    }

    public User getUserByLoginIdOrThrow(String userId){
        return userRepository.findByLoginId(userId)
            .orElseThrow(()->new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    //user ignoreCount increase
    public Long addCurrentIgnoreCount(String loginId){
        User user=userRepository.findByLoginId(loginId)
                .orElseThrow(()->new GeneralException(ErrorStatus.USER_NOT_FOUND));
        user.addCurrentIgnoreCnt();
        return user.getCurrentIgnoreCnt().longValue();
    }
}
