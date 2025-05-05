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
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medication-schedules")
@RequiredArgsConstructor
public class MedicationScheduleController {

    private final MedicationScheduleService scheduleService;

    @PatchMapping("/{scheduleId}")
    @Operation(summary = "복약 여부 체크", description = """
        약 복용 여부를 true로 업데이트합니다. <br>
        복용여부 false일시에만 reason, remindAfterMinutes을 입력받습니다. <br>
        "remindAfterMinutes": 30, 60, null
        """)
    public ResponseEntity<ApiResponse<UpdateMedicationStatusResponse>> updateSchedule(
        Authentication authentication,
        @PathVariable("scheduleId") Long scheduleId,
        @RequestBody UpdateMedicationStatusRequest request) {

        UpdateMedicationStatusResponse response = scheduleService.updateIsTaken(
            authentication.getName(), scheduleId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/today")
    @Operation(summary = "오늘 복약 스케줄 조회")
    public ResponseEntity<ApiResponse<List<MedicationScheduleResponse>>> getMedicationSchedulesToday(
        Authentication authentication) {
        LocalDate today = LocalDate.now();
        List<MedicationScheduleResponse> responses = scheduleService.getSchedulesByDate(
            authentication.getName(), today);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/today/not-taken")
    @Operation(summary = "오늘 복용하지 않은 복약 스케줄 조회")
    public ResponseEntity<ApiResponse<List<MedicationScheduleResponse>>> getTodayNotTakenSchedules(
        Authentication authentication
    ) {
        LocalDate today = LocalDate.now();
        List<MedicationScheduleResponse> responses =
            scheduleService.getNotTakenMedicationSchedules(authentication.getName(), today);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/by-date")
    @Operation(summary = "특정 날짜 복약 스케줄 조회")
    public ResponseEntity<ApiResponse<List<MedicationScheduleResponse>>> getMedicationSchedulesByDate(
        Authentication authentication,
        @Parameter(example = "2025-05-01")
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date
    ) {
        List<MedicationScheduleResponse> responses = scheduleService.getSchedulesByDate(
            authentication.getName(), date);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("by-range")
    @Operation(summary = "날짜 범위 내 복약 스케줄 조회")
    public ResponseEntity<ApiResponse<List<MedicationScheduleResponse>>> getMedicationSchedulesByRange(
        Authentication authentication,
        @Parameter(example = "2025-05-01")
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
        @Parameter(example = "2025-05-03")
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDate
    ) {
        List<MedicationScheduleResponse> schedules = scheduleService.getSchedulesByDateRange(
            authentication.getName(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }

}
