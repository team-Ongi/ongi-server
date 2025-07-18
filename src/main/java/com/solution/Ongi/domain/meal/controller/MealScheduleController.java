package com.solution.Ongi.domain.meal.controller;

import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.dto.*;
import com.solution.Ongi.domain.meal.service.MealScheduleService;
import com.solution.Ongi.global.response.ApiResponse;
import com.solution.Ongi.global.response.code.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meal-schedules")
public class MealScheduleController {

    private final MealScheduleService mealScheduleService;

    //TODO: 리팩터링 시 Mapper 로 today, by-date 코드 최적화

    @Operation(summary = "금일 식사 일정 조회", description = """
            금일 MealSchedule2 목록을 조회합니다.
            반환 항목 : 스케줄 ID, 날짜, 시간, 상태, 연관된 Meal ID
        """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "금일 식사 일정 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MealScheduleResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저(ID)가 존재하지 않는 경우", content = @Content)
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<MealScheduleResponse>>>getMealSchedules(
            Authentication authentication){
        List<MealScheduleResponse> responseList = mealScheduleService
                .getMealSchedulesByExactDate(authentication.getName(),LocalDate.now());

        return ResponseEntity.ok(ApiResponse.success(responseList, SuccessStatus.SUCCESS_200));
    }

    @Operation(summary = "특정 날짜 식사 일정 조회",description = """
            특정 날짜의 MealSchedule2 목록을 조회합니다.
            반환 항목 : 스케줄 ID, 날짜, 시간, 상태, 연관된 Meal ID
        """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "특정 날짜 식사 일정 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MealScheduleResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "날짜 포맷이 올바르지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저(ID)가 존재하지 않는 경우", content = @Content)
    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<List<MealScheduleResponse>>>getMealSchedulesByExactDate(
            Authentication authentication,
            @RequestBody LocalDate date){
        List<MealScheduleResponse> responseList = mealScheduleService
                .getMealSchedulesByExactDate(authentication.getName(),date);

        return ResponseEntity.ok(ApiResponse.success(responseList,SuccessStatus.SUCCESS_200));
    }

    @Operation(summary = "기간 내의 식사 일정 조회",description = """
            startDate와 endDate 사이의 MealSchedule2 목록을 조회합니다.
            반환 항복 : 스케줄 ID, 날짜, 시간, 상태, 연관된 Meal ID
        """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "특정 날짜 식사 일정 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MealScheduleResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "날짜 포맷이 올바르지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저(ID)가 존재하지 않는 경우", content = @Content)
    @GetMapping("/by-term")
    public ResponseEntity<ApiResponse<List<MealScheduleResponse>>> getMealSchedulesByDate(
            Authentication authentication,
            @RequestBody MealScheduleDateTermRequest termRequest) {

        List<MealScheduleResponse> responseList = mealScheduleService
                .getMealSchedulesByDate(authentication.getName(), termRequest.startDate(), termRequest.endDate());

        return ResponseEntity.ok(ApiResponse.success(responseList,SuccessStatus.SUCCESS_200));
    }

    @Operation(summary = "복수 식사 스케줄 업데이트",description = """
                    금일 식사상태들을 true/false로 업데이트 합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "복수 식사 스케줄 상태 업데이트 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateMealScheduleStatusesResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값이 잘못됨.", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스케줄 ID가 존재하지 않는 경우", content = @Content)
    @PatchMapping("/today/status")
    public ResponseEntity<ApiResponse<List<UpdateMealScheduleStatusesResponse>>> updateMultipleMealScheduleStatuses(
            Authentication authentication,
            @RequestBody List<UpdateMealScheduleStatusesRequest> requests){

        List<MealSchedule> todaySchedules= mealScheduleService.getMealSchedulesByUserId(authentication.getName());
        Map<Long,Boolean>requestedStatusMap=requests.stream()
                .collect(Collectors.toMap(
                        UpdateMealScheduleStatusesRequest::getScheduleId,
                        UpdateMealScheduleStatusesRequest::isStatus
                ));

        List<UpdateMealScheduleStatusesResponse> responseList = todaySchedules.stream()
                .filter(ms -> requestedStatusMap.containsKey(ms.getId()))
                .map(ms -> {
                    boolean newStatus = requestedStatusMap.get(ms.getId());
                    mealScheduleService.updateMealScheduleStatus(ms.getId(), newStatus);
                    return new UpdateMealScheduleStatusesResponse(
                            ms.getId(),
                            "스케줄 상태가 업데이트되었습니다."
                    );
                })
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responseList,SuccessStatus.SUCCESS_200));
    }

    @Operation(summary = "단일 식사 스케줄 상태 업데이트",description = """
            스케줄 ID로 특정 식사 스케줄의 상태를 true/false로 업데이트합니다.
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "단일 식사 스케줄 상태 업데이트 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값이 잘못되었습니다.", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스케줄 ID가 존재하지 않는 경우", content = @Content)
    @PatchMapping("/{scheduleId}/status")
    public ResponseEntity<ApiResponse<String>> updateSingleMealScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestBody UpdateMealScheduleStatusRequest request) {

        mealScheduleService.updateMealScheduleStatus(scheduleId, request.isStatus());
        return ResponseEntity.ok(ApiResponse.success("식사 스케줄 상태가 업데이트되었습니다.",SuccessStatus.SUCCESS_200));
    }

}