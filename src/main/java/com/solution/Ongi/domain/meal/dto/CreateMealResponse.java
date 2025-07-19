package com.solution.Ongi.domain.meal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record CreateMealResponse(
        Long mealId,
        String message
) {}
