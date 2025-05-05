package com.solution.Ongi.domain.medication.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateMedicationRequest {
    private String medication_title;
    private List<String> timeList; //HH:mm 형식
}
