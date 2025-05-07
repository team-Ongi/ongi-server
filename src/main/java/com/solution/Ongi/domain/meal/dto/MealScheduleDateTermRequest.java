package com.solution.Ongi.domain.meal.dto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public record MealScheduleDateTermRequest(
    @RequestParam("startDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate,

    @RequestParam("endDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate
) {}
