package com.solution.Ongi.domain.medication.dto;

import com.solution.Ongi.domain.medication.Medication;
import java.time.LocalTime;
import java.util.List;

public record MedicationDTO(Long id, String title, List<LocalTime> timeList) {
    public MedicationDTO(Medication medication) {
        this(medication.getId(), medication.getMedication_title(), medication.getMedication_time());
    }
}