package com.solution.Ongi.domain.medication.controller;

import com.solution.Ongi.domain.medication.dto.CreateFixedTimeMedicationRequest;
import com.solution.Ongi.domain.medication.dto.CreateMealBasedMedicationRequest;
import com.solution.Ongi.domain.medication.dto.CreateMedicationResponse;
import com.solution.Ongi.domain.medication.dto.UpdateFixedTimeMedicationRequest;
import com.solution.Ongi.domain.medication.dto.UpdateMealBasedMedicationRequest;
import com.solution.Ongi.domain.medication.service.MedicationService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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
        - `medicationName` : 약 이름 (예: 감기약) <br>
        - `timeList` : 복용 시간 리스트 (형식: "HH:mm", 예: ["08:00", "21:00"]) <br>
        - 시:분 형식의 문자열로 전달해야 합니다. <br><br>
        ```
        {
          "medicationName": "감기약",
          "timeList": ["12:30", "20:00"]
        }
        ```
        """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "정시 복용 약 등록 성공", content = @Content(mediaType = "application/json",schema =@Schema(implementation = CreateMedicationResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
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
        - `medicationName` : 약 이름 (예: 비타민) <br>
        - `intakeTiming` : 복용 타이밍 (식전: `BEFORE_MEAL`, 식후: `AFTER_MEAL`) <br>
        - `mealTypes` : 해당되는 끼니 리스트 (아침: `BREAKFAST`, 점심: `LUNCH`, 저녁: `DINNER`) <br>
        - `remindAfterMinutes` : 복용하지 않았을 경우 알림을 보낼 간격 (30 또는 60 분) <br><br>
         예시:
        ```
        {
          "medicationName": "비타민",
          "intakeTiming": "AFTER_MEAL",
          "mealTypes": ["LUNCH", "DINNER"],
          "remindAfterMinutes": 30
        }
        ```
        """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "식전/식후 복용 약 등록 성공", content = @Content(mediaType = "application/json",schema =@Schema(implementation = CreateMedicationResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "식사시간이 등록되어 있지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
    public ResponseEntity<ApiResponse<CreateMedicationResponse>> createMealBasedMedication(
        Authentication authentication,
        @RequestBody @Valid CreateMealBasedMedicationRequest request) {
        CreateMedicationResponse response = medicationService.createMealBasedMedication(authentication.getName(), request);

        URI location = URI.create("/medications/" + response.medicationId());

        return ResponseEntity
            .created(location) // Location 헤더 포함
            .body(ApiResponse.success(response));
    }

    // 정시 복용 약 수정
    @PutMapping("/fixed-time/{medicationId}")
    @Operation(summary = "정시 복용 약 수정",
        description = """
        정해진 시각에 복용하는 약 정보를 수정합니다. <br><br>
        - `medicationName` : 변경할 약 이름 <br>
        - `time` : 변경할 약의 복용 시간  (형식: "HH:mm", 예: "08:00" ) <br><br>
        예시:
        ```
        {
          "medicationName": "혈압약",
          "time": "07:00"
        }
        ```
        """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "정시 복용 약 수정 성공", content = @Content(mediaType = "application/json",schema =@Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않거나 약 정보가 존재하지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
    public ResponseEntity<ApiResponse<String>> updateFixedTimeMedication(
        Authentication authentication,
        @PathVariable Long medicationId,
        @RequestBody UpdateFixedTimeMedicationRequest request
    ) {
        medicationService.updateFixedTimeMedication(authentication.getName(), medicationId, request);
        return ResponseEntity.ok(ApiResponse.success("정시 복용 약이 수정되었습니다."));
    }

    // 식전/식후 약 수정
    @PutMapping("/meal-based/{medicationId}")
    @Operation(summary = "식전/식후 약 수정",
        description = """
        식전/식후 복용 약 정보를 수정합니다. <br><br>
        - `medicationName` : 변경할 약 이름 <br>
        - `intakeTiming` : `BEFORE_MEAL` 또는 `AFTER_MEAL` <br>
        - `mealType` : 수정할 끼니 정보 <br>
        - `remindAfterMinutes` : 복용하지 않았을 경우 알림을 보낼 간격 (30 또는 60 분) <br><br>
        예시:
        ```
        {
          "medicationName": "비타민",
          "intakeTiming": "BEFORE_MEAL",
          "mealType": "BREAKFAST",
          "remindAfterMinutes": 60
        }
        ```
        """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "식전/식후 복용 약 수정 성공", content = @Content(mediaType = "application/json",schema =@Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않거나 약 정보가 존재하지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
    public ResponseEntity<ApiResponse<String>> updateMealBasedMedication(
        Authentication authentication,
        @PathVariable Long medicationId,
        @RequestBody UpdateMealBasedMedicationRequest request
    ) {
        medicationService.updateMealBasedMedication(authentication.getName(), medicationId, request);
        return ResponseEntity.ok(ApiResponse.success("식사 기반 약이 수정되었습니다."));
    }

    // Medication 삭제
    @DeleteMapping("/{medicationId}")
    @Operation(summary = "복용 약 정보 삭제")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "식전/식후 복용 약 삭제 성공", content = @Content(mediaType = "application/json",schema =@Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "약을 삭제할 권한이 없음", content = @Content(mediaType = "application/json",schema =@Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않거나 약 정보가 존재하지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
    public ResponseEntity<ApiResponse<String>> deleteMedication(
        Authentication authentication,
        @PathVariable Long medicationId) {
        medicationService.deleteMedication(authentication.getName(), medicationId);
        return ResponseEntity.ok(ApiResponse.success("성공적으로 삭제되었습니다."));
    }

}

