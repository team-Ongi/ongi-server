package com.solution.Ongi.domain.user.service;

import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.UsersMealVoice;
import com.solution.Ongi.domain.user.UsersMedicationVoice;
import com.solution.Ongi.domain.user.dto.*;
import com.solution.Ongi.domain.user.enums.LoginMode;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.domain.user.repository.UsersMealVoiceRepository;
import com.solution.Ongi.domain.user.repository.UsersMedicationVoiceRepository;
import com.solution.Ongi.domain.user.repository.projection.NotTakenStatsProjection;
import com.solution.Ongi.global.jwt.JwtTokenProvider;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import com.solution.Ongi.infra.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final WebClient fastApiWebClient;
    private final S3Service s3Service;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final UsersMedicationVoiceRepository usersMedicationVoiceRepository;
    private final UsersMealVoiceRepository usersMealVoiceRepository;


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

            fastApiWebClient.post()
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

    // 유저 녹음 파일 조회
    public UserVoiceResponse getUserVoice(String loginId){
        User user = getUserByLoginIdOrThrow(loginId);

        List<String> usersMealVoiceList = usersMealVoiceRepository.findVoiceFileUrlByUserId(user.getId());
        List<String> usersMedicationVoiceList = usersMedicationVoiceRepository.findVoiceFileUrlByUserId(user.getId());
        return new UserVoiceResponse(usersMealVoiceList,usersMedicationVoiceList);
    }

    // 보호자 음성 삭제
    public void deleteUserVoice(String loginId){
        User user = getUserByLoginIdOrThrow(loginId);

        // S3에 등록된 파일 삭제 && DB에 등록된 음성 파일 경로 삭제
        List<UsersMealVoice> usersMealVoiceList = usersMealVoiceRepository.findAllByUserId(user.getId());
        for(UsersMealVoice usersMealVoice:usersMealVoiceList) {
            String filePath = extractS3KeyFromUrl(usersMealVoice.getVoiceFileUrl());
            s3Service.deleteFile(filePath);
        }
        usersMealVoiceRepository.deleteAllByUserId(user.getId());

        List<UsersMedicationVoice> usersMedicationVoiceList = usersMedicationVoiceRepository.findAllByUserId(user.getId());
        for(UsersMedicationVoice usersMedicationVoice:usersMedicationVoiceList) {
            String filePath = extractS3KeyFromUrl(usersMedicationVoice.getVoiceFileUrl());
            s3Service.deleteFile(filePath);
        }
        usersMedicationVoiceRepository.deleteAllByUserId(user.getId());

        // DB에 등록된 유저의 음성 목소리 파일 키 삭제
        user.updateVoiceFileKey(null);
        userRepository.save(user);
    }

    private String extractS3KeyFromUrl(String url) {
        // S3 도메인 기준으로 split → 뒷부분이 key
        String prefix = ".amazonaws.com/";
        int index = url.indexOf(prefix);

        if (index == -1) {
            throw new IllegalArgumentException("유효한 S3 경로가 아닙니다: " + url);
        }

        return url.substring(index + prefix.length());
    }

    // 유저의 각 달 복약 스케줄 조회
    public UserMedicationScheduleByRangeResponse getUserMedicationSchedulesByDateRange(String loginId, LocalDate startDate) {
        User user = getUserByLoginIdOrThrow(loginId);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<NotTakenStatsProjection> notTakenStatsByDateRange = medicationScheduleRepository.getNotTakenStatsByDateRange(user.getId(), startDate,endDate);
        List<String> notTakenMedicationDates = new ArrayList<>();

        for(NotTakenStatsProjection data : notTakenStatsByDateRange){
            if(data.getNotTakenCount() >0 )
                notTakenMedicationDates.add(data.getDate().toString());
        }
        return new UserMedicationScheduleByRangeResponse(notTakenMedicationDates);
    }

}
