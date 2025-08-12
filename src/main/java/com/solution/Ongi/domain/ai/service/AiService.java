package com.solution.Ongi.domain.ai.service;

import com.solution.Ongi.domain.ai.dto.GenerateEldercareFeedbackRequest;
import com.solution.Ongi.domain.ai.dto.GenerateFeedbackRequest;
import com.solution.Ongi.domain.ai.dto.GenerateFeedbackResponse;
import com.solution.Ongi.domain.ai.dto.PostEldercareFeedbackResponse;
import com.solution.Ongi.domain.medication.dto.MedicationInfoFromFastAPIResponse;
import com.solution.Ongi.domain.medication.service.MedicationService;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final UserService userService;
    private final MedicationService medicationService;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GENERATE_FEEDBACK_URL = "http://3.37.125.234:8000/ai/generate/feedback"; // AI 기능 1번 호출
    private final WebClient fastApiWebClient;



    public PostEldercareFeedbackResponse generateFeedback(String loginId, GenerateFeedbackRequest request){
        User user = userService.getUserByLoginIdOrThrow(loginId);
        GenerateEldercareFeedbackRequest generateEldercareFeedbackRequest = new GenerateEldercareFeedbackRequest(
                user.getId(),
                37.5665F,
                126.9780F,
                request.todayWalkCount()
        );
        GenerateFeedbackResponse response = getFeedback(generateEldercareFeedbackRequest);
        return new PostEldercareFeedbackResponse(response.walks_change(),response.yesterday_walks(), response.today_walks(),response.yesterday_distance_km(), response.today_distance_km(), response.trend_3days(), response.recommended_time(), response.recommend_walk(), response.comment());
    }

    public String extractText(String loginId, MultipartFile file){
        User user = userService.getUserByLoginIdOrThrow(loginId);
        try{
            // MultipartBody 생성
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new InputStreamResource(file.getInputStream())).filename(Objects.requireNonNull(file.getOriginalFilename()));

            MedicationInfoFromFastAPIResponse response = fastApiWebClient.post()
                    .uri("/ai/ocr/extract-text")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(MedicationInfoFromFastAPIResponse.class)
                    .block();

            assert response != null;
            medicationService.generateMedication(user, response);
            return "";
        }
        catch(Exception e){
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private GenerateFeedbackResponse getFeedback(GenerateEldercareFeedbackRequest requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GenerateEldercareFeedbackRequest> entity = new HttpEntity<>(requestDto, headers);

        try {
            ResponseEntity<GenerateFeedbackResponse> response = restTemplate.exchange(
                    GENERATE_FEEDBACK_URL,
                    HttpMethod.POST,
                    entity,
                    GenerateFeedbackResponse.class
            );
            return response.getBody();  // GPT의 피드백 문자열
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 서버 호출 실패: " + e.getMessage(), e);
        }
    }
}
