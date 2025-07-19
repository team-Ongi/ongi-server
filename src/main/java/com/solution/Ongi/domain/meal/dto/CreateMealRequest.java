package com.solution.Ongi.domain.meal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public record CreateMealRequest(

        @NotBlank
        String meal_type,

        @NotBlank
        String meal_time
) {}
