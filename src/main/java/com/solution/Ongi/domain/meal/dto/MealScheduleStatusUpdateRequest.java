package com.solution.Ongi.domain.meal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MealScheduleStatusUpdateRequest {
    private Long scheduleId;
    private boolean status;
}
