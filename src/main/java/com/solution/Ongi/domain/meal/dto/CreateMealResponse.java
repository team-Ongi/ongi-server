package com.solution.Ongi.domain.meal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateMealResponse {
    private Long mealId;
    private String message;
}
