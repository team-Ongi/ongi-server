package com.solution.Ongi.domain.meal.dto;

public record UpdateMealScheduleStatusRequest (
        Boolean status,
        String reason
){ }