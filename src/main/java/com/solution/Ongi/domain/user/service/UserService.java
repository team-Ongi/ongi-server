package com.solution.Ongi.domain.user.service;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.dto.UserInfoResponse;
import com.solution.Ongi.domain.user.enums.LoginMode;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.global.jwt.JwtTokenProvider;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final WebClient fastApiWebClient;


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

    //user currentIgnoreCnt +1
    public Long addUserCurrentIgnoreCount(String loginId){
        User user=userRepository.findByLoginId(loginId)
                .orElseThrow(()->new GeneralException(ErrorStatus.USER_NOT_FOUND));
        user.addCurrentIgnoreCnt();
        return user.getCurrentIgnoreCnt().longValue();
    }

    //user ignoreCnt 설정
    public Long setUserIgnoreCnt(String loginId,Integer ignoreCnt){
        User user=userRepository.findByLoginId(loginId)
                .orElseThrow(()->new GeneralException(ErrorStatus.USER_NOT_FOUND));

        user.updateIgnoreCnt(ignoreCnt);
        return user.getIgnoreCnt().longValue();
    }

    // 회원 탈퇴
    public void deleteUser(String loginId){
        User user = getUserByLoginIdOrThrow(loginId);
        userRepository.delete(user);
    }

    // 음성 녹음
    public void recordVoice(MultipartFile file, String loginId){
        User user = getUserByLoginIdOrThrow(loginId);
        try{
            // MultipartBody 생성
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new InputStreamResource(file.getInputStream())).filename(Objects.requireNonNull(file.getOriginalFilename()));
            builder.part("user_id", String.valueOf(user.getId()));

            String response = fastApiWebClient.post()
                    .uri("/voice/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
        catch(Exception e){
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
