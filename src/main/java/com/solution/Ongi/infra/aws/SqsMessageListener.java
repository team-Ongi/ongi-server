package com.solution.Ongi.infra.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solution.Ongi.domain.ai.dto.GenerateMedicationToSqsRequest;
import com.solution.Ongi.domain.medication.service.MedicationService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor()
@Slf4j
public class SqsMessageListener {

    private final ObjectMapper objectMapper;
    private final MedicationService medicationService;

    @SqsListener("extract-medication-queue")
    public void receiveMessage(String message) {
        log.info("받은 메시지: " + message);
        try {
            GenerateMedicationToSqsRequest response =
                    objectMapper.readValue(message, GenerateMedicationToSqsRequest.class);

            // 처리 로직
            medicationService.generateMedication(response);

        } catch (Exception e) {
            log.error("SQS 메시지 역직렬화 실패", e);
        }

    }
}
