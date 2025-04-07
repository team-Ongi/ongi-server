package com.solution.Ongi.domain.medication.controller;

import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.service.MedicationService;
import com.solution.Ongi.domain.medication.dto.CreateMedicationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medications")
@RequiredArgsConstructor
public class MedicationController {
    private final MedicationService medicationService;

    @PostMapping("/post/{user_id}")
    public ResponseEntity<Long> createMedication(@PathVariable("user_id") Long user_id,
                                                 @RequestBody CreateMedicationRequest createMedicationRequest) {
        Medication medication=medicationService.createMedication(user_id,createMedicationRequest);
        return ResponseEntity.ok(user_id);
    }
    //TODO: 확장성 위해 PathVariable -> RequestParam 바꾸자

    @GetMapping("/get/{user_id}")
    public ResponseEntity<List<Medication>> getAllMedications(@PathVariable("user_id") Long user_id) {
        List<Medication> medications = medicationService.getAllMedication(user_id);
        return ResponseEntity.ok(medications);
    }

    // Meal 삭제 엔드포인트
    @DeleteMapping("/delete/{medication_id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long medication_id) {
        medicationService.deleteMedication(medication_id);
        return ResponseEntity.noContent().build();
    }
}
