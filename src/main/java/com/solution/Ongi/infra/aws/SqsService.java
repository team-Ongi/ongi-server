package com.solution.Ongi.infra.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@RequiredArgsConstructor
public class SqsService {

    private final SqsAsyncClient sqsClient;

    private static final String QUEUE_URL = "https://sqs.ap-northeast-2.amazonaws.com/454547470339/extract-medication-queue";

    public void sendMessage(String messageBody) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(messageBody)
                .build();

        sqsClient.sendMessage(request);
    }

}
