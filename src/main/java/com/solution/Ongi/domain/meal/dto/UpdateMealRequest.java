package com.solution.Ongi.domain.meal.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMealRequest (
        @NotBlank
        String meal_type,

        @NotBlank
        String meal_time
){
}
