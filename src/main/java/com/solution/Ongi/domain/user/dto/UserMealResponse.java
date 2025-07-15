package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.meal.dto.MealResponse;

import java.util.List;

public record UserMealResponse(
        List<MealResponse> userMealList
) {
}
