package com.solution.Ongi.domain.medication.dto;

import java.util.List;

public record UpdateFixedTimeMedicationRequest(
    String title,
    List<String> timeList
) {

}
