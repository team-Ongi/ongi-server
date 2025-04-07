package com.solution.Ongi.domain.meal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMealRequest {
    private String meal_type;
    private String meal_time; //HH:mm 형식
}
