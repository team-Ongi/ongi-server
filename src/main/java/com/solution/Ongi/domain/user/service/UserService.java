package com.solution.Ongi.domain.user.service;

import com.solution.Ongi.domain.smsverification.SmsVerificationRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.dto.UserInfoResponse;
import com.solution.Ongi.domain.user.enums.LoginMode;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.global.jwt.JwtTokenProvider;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;


    public UserInfoResponse getUserInfoWithMode(String token, String loginId) {
        LoginMode mode = LoginMode.valueOf(jwtProvider.getModeFromToken(token));
        User user = getUserByLoginIdOrThrow(loginId);
        return UserInfoResponse.from(user, mode);
    }

    public void markGuardianAgreement(String loginId) {
        User user = getUserByLoginIdOrThrow(loginId);
        user.getAgreement().markPersonalInfoAgreement();
    }

    public User getUserByIdOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(()->new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }


    public User getUserByLoginIdOrThrow(String loginId){
        return userRepository.findByLoginId(loginId)
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
