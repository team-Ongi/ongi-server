package com.solution.Ongi.domain.medication.controller;

import com.solution.Ongi.domain.medication.dto.MedicationScheduleResponse;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationStatusRequest;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationStatusResponse;
import com.solution.Ongi.domain.medication.service.MedicationScheduleService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medication-schedules")
@RequiredArgsConstructor
public class MedicationScheduleController {

    private final MedicationScheduleService scheduleService;

    @PutMapping("/{scheduleId}")
    @Operation(summary = "복약 여부 체크", description = """
        약 복용 여부를 true로 업데이트합니다. <br>
        복용여부 false일시에만 reason, remindAfterMinutes을 입력받습니다. <br>
        "remindAfterMinutes": 30, 60, null
        """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "복약 여부 체크 완료", content = @Content(mediaType = "application/json",schema =@Schema(implementation = UpdateMedicationStatusResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "복약 여부 체크 권한 없음", content = @Content(mediaType = "application/json",schema =@Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않거나 복약 스케줄 정보가 존재하지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
    public ResponseEntity<ApiResponse<UpdateMedicationStatusResponse>> updateSchedule(
        Authentication authentication,
        @PathVariable("scheduleId") Long scheduleId,
        @RequestBody UpdateMedicationStatusRequest request) {

        UpdateMedicationStatusResponse response = scheduleService.updateIsTaken(
            authentication.getName(), scheduleId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
