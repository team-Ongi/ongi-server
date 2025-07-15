package com.solution.Ongi.domain.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record MealScheduleDateTermRequest(

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate startDate,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate endDate

) {}
