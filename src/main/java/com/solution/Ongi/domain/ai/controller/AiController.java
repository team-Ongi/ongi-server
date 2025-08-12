package com.solution.Ongi.domain.ai.controller;

import com.solution.Ongi.domain.ai.dto.GenerateFeedbackRequest;
import com.solution.Ongi.domain.ai.dto.PostEldercareFeedbackResponse;
import com.solution.Ongi.domain.ai.service.AiService;
import com.solution.Ongi.global.response.ApiResponse;
import com.solution.Ongi.global.response.code.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate/feedback")
    @Operation(description = "유니톤 AI 기능 1번")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "행동기반 AI 성공", content = @Content(schema = @Schema(implementation =PostEldercareFeedbackResponse.class )))
    public ResponseEntity<ApiResponse<PostEldercareFeedbackResponse>> createElderFeedback(@RequestBody @Valid GenerateFeedbackRequest generateFeedbackRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PostEldercareFeedbackResponse response = aiService.generateFeedback(authentication.getPrincipal().toString(),generateFeedbackRequest);
        return ResponseEntity.ok(ApiResponse.success(response, SuccessStatus.SUCCESS_200));
    }

    @PostMapping(value = "/ocr/extract-text",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "유니톤 AI 기능 2번")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "행동기반 AI 성공", content = @Content(schema = @Schema(implementation =PostEldercareFeedbackResponse.class )))
    public ResponseEntity<ApiResponse<String>> extractText(@RequestParam("file") MultipartFile file ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        aiService.extractText(authentication.getPrincipal().toString(),file);
        return ResponseEntity.ok(ApiResponse.success("",SuccessStatus.SUCCESS_200));
    }

}
