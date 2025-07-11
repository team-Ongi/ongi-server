package com.solution.Ongi.domain.schedule.dto;

import java.time.LocalDate;

public record CalendarDayStatusResponse (
    LocalDate date,
    boolean mealMissed,
    boolean medicationMissed
){}
