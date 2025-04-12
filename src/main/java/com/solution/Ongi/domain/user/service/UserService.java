package com.solution.Ongi.domain.user.service;

import com.solution.Ongi.domain.agreement.Agreement;
import com.solution.Ongi.global.jwt.JwtTokenProvider;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.dto.LoginRequest;
import com.solution.Ongi.domain.user.dto.SignupRequest;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signup(SignupRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
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
            .agreement(agreement)
            .build();


        user.encodePassword(passwordEncoder);

        return userRepository.save(user);

    }

    public String login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.id())
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        return jwtProvider.createToken(user.getLoginId());
    }

    public String isDuplicatedId(String loginId) {
        boolean exists = userRepository.existsByLoginId(loginId);
        return exists ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.";
    }
}
