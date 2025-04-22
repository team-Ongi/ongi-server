package com.solution.Ongi.domain.medication.controller;

import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.dto.CreateMedicationResponse;
import com.solution.Ongi.domain.medication.service.MedicationService;
import com.solution.Ongi.domain.medication.dto.CreateMedicationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @PostMapping("/post/{userId}/medications")
    public ResponseEntity<CreateMedicationResponse> createMedication(
            @PathVariable("userId") Long userId,
            @RequestBody CreateMedicationRequest createMedicationRequest) {

        Medication medication=medicationService.createMedication(userId,createMedicationRequest);

        URI location= URI.create("/users/"+userId+"/medications/"+medication.getId());

        return ResponseEntity
                .created(location)
                .body(new CreateMedicationResponse(medication.getId(),"복약이 등록되었습니다."));
    }

    @GetMapping("/users/{user_id}/medications")
    public ResponseEntity<List<Medication>> getAllMedications(@PathVariable("user_id") Long user_id) {
        List<Medication> medications = medicationService.getAllMedication(user_id);
        return ResponseEntity.ok(medications);
    }

    // Meal 삭제 엔드포인트
    @DeleteMapping("/delete/{medication_id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long medication_id) {
        medicationService.deleteMedication(medication_id);
        return ResponseEntity.noContent().build();//204 no
    }
}
