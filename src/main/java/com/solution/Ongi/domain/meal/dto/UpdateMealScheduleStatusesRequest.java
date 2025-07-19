package com.solution.Ongi.domain.meal.dto;

public record UpdateMealScheduleStatusesRequest(
    Long scheduleId,
    boolean status
){}
