package com.solution.Ongi.domain.meal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public record UpdateMealScheduleStatusesRequest(
        Long scheduleId,
        boolean status
) { }
