package com.solution.Ongi.domain.meal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMealScheduleStatusesRequest {
    private Long scheduleId;
    private boolean status;
}
