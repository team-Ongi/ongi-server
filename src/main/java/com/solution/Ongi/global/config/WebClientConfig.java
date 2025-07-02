package com.solution.Ongi.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient fastApiWebClient() {
        return WebClient.builder()
                .baseUrl("http://3.37.125.234:8000")
                .build();
    }
}
