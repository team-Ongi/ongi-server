package com.solution.Ongi.domain.medication.dto;

import java.util.List;

public record UpdateMedicationRequest(
    String title,               // 약 이름
    List<String> timeList    // 복용 시간 리스트
) {}