package com.solution.Ongi.domain.meal.dto;

public record UpdateMealScheduleStatusResponse(
        Long id,
        Boolean status,
        String message
) {
}
