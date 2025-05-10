package com.solution.Ongi.domain.medication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateFixedTimeMedicationRequest(
    @NotBlank
    String title,
    @NotEmpty
    List<String> timeList
) {

}
