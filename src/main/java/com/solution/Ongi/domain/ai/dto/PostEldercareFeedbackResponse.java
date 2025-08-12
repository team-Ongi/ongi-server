package com.solution.Ongi.domain.ai.dto;

public record PostEldercareFeedbackResponse(
        String walksChange,
        int yesterdayWalks,
        int todayWalks,
        double yesterdayDistanceKm,
        double todayDistanceKm,
        String trend3days,
        String recommendedTime,
        boolean recommendWalk,
        String comment
) {
}
