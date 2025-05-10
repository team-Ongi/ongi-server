package com.solution.Ongi.domain.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.enums.MealType;

import java.time.LocalTime;

public record MealResponse (
        Long mealId,
        MealType mealType,
        @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "HH:mm")
        LocalTime mealTime
){
    public static MealResponse from(Meal meal){
        return new MealResponse(
                meal.getId(),
                meal.getMealType(),
                meal.getMealTime()
        );
    }
}