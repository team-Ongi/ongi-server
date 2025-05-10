package com.solution.Ongi.domain.medication.controller;

import com.solution.Ongi.domain.medication.dto.CreateFixedTimeMedicationRequest;
import com.solution.Ongi.domain.medication.dto.CreateMealBasedMedicationRequest;
import com.solution.Ongi.domain.medication.dto.CreateMedicationResponse;
import com.solution.Ongi.domain.medication.dto.MedicationResponseDTO;
import com.solution.Ongi.domain.medication.dto.UpdateFixedTimeMedicationRequest;
import com.solution.Ongi.domain.medication.dto.UpdateMealBasedMedicationRequest;
import com.solution.Ongi.domain.medication.service.MedicationService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    // 정시 복용 약 등록
    @PostMapping("/fixed-time")
    @Operation(summary = "정시 복용 약 등록",
        description = """
        정해진 시각에 복용하는 약을 등록합니다. <br><br>
        - `title` : 약 이름 (예: 감기약) <br>
        - `timeList` : 복용 시간 리스트 (형식: "HH:mm", 예: ["08:00", "21:00"]) <br>
        - 시:분 형식의 문자열로 전달해야 합니다. <br><br>
        ```
        {
          "title": "감기약",
          "timeList": ["12:30", "20:00"]
        }
        ```
        """)
    public ResponseEntity<ApiResponse<CreateMedicationResponse>> createFixedTimeMedication(
        Authentication authentication,
        @RequestBody @Valid CreateFixedTimeMedicationRequest request) {
        CreateMedicationResponse response = medicationService.createFixedTimeMedication(authentication.getName(), request);

        URI location = URI.create("/medications/" + response.medicationId());

        return ResponseEntity
            .created(location)
            .body(ApiResponse.success(response));
    }

    // 식전/식후 복용 약 등록
    @PostMapping("/meal-based")
    @Operation(summary = "식전/식후 복용 약 등록",
        description = """
        식전/식후 약을 등록합니다. <br><br>
        - `title` : 약 이름 (예: 비타민) <br>
        - `intakeTiming` : 복용 타이밍 (식전: `BEFORE_MEAL`, 식후: `AFTER_MEAL`) <br>
        - `mealTypes` : 해당되는 끼니 리스트 (아침: `BREAKFAST`, 점심: `LUNCH`, 저녁: `DINNER`) <br>
        - `remindAfterMinutes` : 복용하지 않았을 경우 알림을 보낼 간격 (30 또는 60 분) <br><br>
         예시:
        ```
        {
          "title": "비타민",
          "intakeTiming": "AFTER_MEAL",
          "mealTypes": ["LUNCH", "DINNER"],
          "remindAfterMinutes": 30
        }
        ```
        """)
    public ResponseEntity<ApiResponse<CreateMedicationResponse>> createMealBasedMedication(
        Authentication authentication,
        @RequestBody @Valid CreateMealBasedMedicationRequest request) {
        CreateMedicationResponse response = medicationService.createMealBasedMedication(authentication.getName(), request);

        URI location = URI.create("/medications/" + response.medicationId());

        return ResponseEntity
            .created(location) // Location 헤더 포함
            .body(ApiResponse.success(response));
    }

    @PutMapping("/fixed-time/{medicationId}")
    @Operation(summary = "정시 복용 약 수정",
        description = """
        정해진 시각에 복용하는 약 정보를 수정합니다. <br><br>
        - `title` : 변경할 약 이름 <br>
        - `timeList` : 변경할 복용 시간 리스트 (형식: "HH:mm", 예: ["08:00", "21:00"]) <br><br>
        예시:
        ```
        {
          "title": "혈압약",
          "timeList": ["07:00", "19:00"]
        }
        ```
        """)
    public ResponseEntity<ApiResponse<String>> updateFixedTimeMedication(
        Authentication authentication,
        @PathVariable Long medicationId,
        @RequestBody UpdateFixedTimeMedicationRequest request
    ) {
        medicationService.updateFixedTimeMedication(authentication.getName(), medicationId, request);
        return ResponseEntity.ok(ApiResponse.success("정시 복용 약이 수정되었습니다."));
    }

    @PutMapping("/meal-based/{medicationId}")
    @Operation(summary = "식전/식후 약 수정",
        description = """
        식전/식후 복용 약 정보를 수정합니다. <br><br>
        - `title` : 변경할 약 이름 <br>
        - `intakeTiming` : `BEFORE_MEAL` 또는 `AFTER_MEAL` <br>
        - `mealTypes` : 수정할 끼니 정보 리스트 <br>
        - `remindAfterMinutes` : 복용하지 않았을 경우 알림을 보낼 간격 (30 또는 60 분) <br><br>
        예시:
        ```
        {
          "title": "비타민",
          "intakeTiming": "BEFORE_MEAL",
          "mealTypes": ["BREAKFAST"],
          "remindAfterMinutes": 60
        }
        ```
        """)
    public ResponseEntity<ApiResponse<String>> updateMealBasedMedication(
        Authentication authentication,
        @PathVariable Long medicationId,
        @RequestBody UpdateMealBasedMedicationRequest request
    ) {
        medicationService.updateMealBasedMedication(authentication.getName(), medicationId, request);
        return ResponseEntity.ok(ApiResponse.success("식사 기반 약이 수정되었습니다."));
    }

    // 모든 Medication 조회
    @GetMapping
    @Operation(summary = "사용자의 모든 약 정보 조회",
        description = "현재 로그인한 사용자의 전체 약 정보를 조회합니다. "
            + "약 종류에 따라 정시 복용 시간(timeList) 또는 식전/식후 약 복용 정보(intakeTiming, mealTypeList, remindAfterMinutes)를 포함합니다.")
    public ResponseEntity<ApiResponse<List<MedicationResponseDTO>>> getMedications(Authentication authentication) {
        List<MedicationResponseDTO> medicationResponseDTOS = medicationService.getAllMedication(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(medicationResponseDTOS));
    }

    // Medication 삭제
    @DeleteMapping("/{medicationId}")
    @Operation(summary = "복용 약 정보 삭제")
    public ResponseEntity<ApiResponse<String>> deleteMedication(
        Authentication authentication,
        @PathVariable Long medicationId) {
        medicationService.deleteMedication(authentication.getName(), medicationId);
        return ResponseEntity.ok(ApiResponse.success("성공적으로 삭제되었습니다."));
    }

}

