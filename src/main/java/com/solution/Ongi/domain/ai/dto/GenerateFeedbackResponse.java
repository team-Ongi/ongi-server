package com.solution.Ongi.domain.ai.dto;

import java.time.LocalDateTime;

public record GenerateFeedbackResponse(
        String walks_change,
        int yesterday_walks,
        int today_walks,
        double yesterday_distance_km,
        double today_distance_km,
        String trend_3days,
        boolean recommend_walk,
        String recommended_time,
        String comment,
        LocalDateTime updated_at
) {
}
