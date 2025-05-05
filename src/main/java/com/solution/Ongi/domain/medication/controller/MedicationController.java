package com.solution.Ongi.domain.medication.controller;

import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.dto.CreateMedicationRequest;
import com.solution.Ongi.domain.medication.dto.CreateMedicationResponse;
import com.solution.Ongi.domain.medication.dto.MedicationDTO;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationRequest;
import com.solution.Ongi.domain.medication.service.MedicationService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    // Medication 등록
    @PostMapping
    @Operation(summary = "복용 약 정보 등록")
    public ResponseEntity<CreateMedicationResponse> createMedication(
        Authentication authentication,
        @RequestBody CreateMedicationRequest createMedicationRequest) {

        Medication medication=medicationService.createMedication(authentication.getName(),createMedicationRequest);

        URI location= URI.create("/users/"+authentication.getName()+"/medications/"+medication.getId());

        return ResponseEntity
                .created(location)
                .body(new CreateMedicationResponse(medication.getId(),"복약이 등록되었습니다."));
    }

    // 모든 Medication 조회
    @GetMapping
    @Operation(summary = "사용자의 모든 약 정보 조회")
    public ResponseEntity<ApiResponse<List<MedicationDTO>>> getMedications(Authentication authentication) {
        List<MedicationDTO> medicationDTOS = medicationService.getAllMedication(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(medicationDTOS));
    }

    // Medication 삭제
    @DeleteMapping("/{medication_id}")
    @Operation(summary = "복용 약 정보 삭제")
    public ResponseEntity<ApiResponse<String>> deleteMedication(
        Authentication authentication,
        @PathVariable Long medication_id) {
        medicationService.deleteMedication(authentication.getName(), medication_id);
        return ResponseEntity.ok(ApiResponse.success("성공적으로 삭제되었습니다."));
    }

    @PutMapping("/{medication_id}")
    @Operation(summary = "복용 약 정보 수정")
    public ResponseEntity<ApiResponse<String>> updateMedication(
        Authentication authentication,
        @PathVariable Long medication_id,
        @RequestBody UpdateMedicationRequest request) {
        medicationService.updateMedication(authentication.getName(), medication_id, request);
        return ResponseEntity.ok(ApiResponse.success("복약 정보가 수정되었습니다."));
    }
}

