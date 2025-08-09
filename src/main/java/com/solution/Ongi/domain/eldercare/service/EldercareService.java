package com.solution.Ongi.domain.eldercare.service;

import com.solution.Ongi.domain.eldercare.dto.GenerateEldercareFeedbackRequest;
import com.solution.Ongi.domain.eldercare.dto.GenerateFeedbackResponse;
import com.solution.Ongi.domain.eldercare.dto.PostEldercareFeedbackRequest;
import com.solution.Ongi.domain.eldercare.dto.PostEldercareFeedbackResponse;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class EldercareService {

    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "http://3.37.125.234:8000/eldercare/feedback"; // 실제 서버 IP/도메인


    public PostEldercareFeedbackResponse createElderFeedback(String loginId, PostEldercareFeedbackRequest request){
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

    private GenerateFeedbackResponse getFeedback(GenerateEldercareFeedbackRequest requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GenerateEldercareFeedbackRequest> entity = new HttpEntity<>(requestDto, headers);

        try {
            ResponseEntity<GenerateFeedbackResponse> response = restTemplate.exchange(
                    API_URL,
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
