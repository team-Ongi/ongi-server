package com.solution.Ongi.domain.meal.controller;


import com.solution.Ongi.domain.meal.MealSchedule2;
import com.solution.Ongi.domain.meal.dto.*;
import com.solution.Ongi.domain.meal.service.MealSchedule2Service;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meal-schedules2")
@RequiredArgsConstructor
public class MealSchedule2Controller {

    private final MealSchedule2Service mealSchedule2Service;

    @Operation(summary = "금일 식사 일정 조회", description = """
            금일 MealSchedule2 목록을 조회합니다.
            반환 항목 : 스케줄 ID, 날짜, 시간, 상태, 연관된 Meal ID
        """
    )
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<MealSchedule2Response>>>getMealSchedules(
            Authentication authentication){
        List<MealSchedule2> schedules=mealSchedule2Service.getMealSchedulesByUserId(authentication.getName());

        List<MealSchedule2Response> responseList = schedules.stream()
                .map(s -> new MealSchedule2Response(
                        s.getId(),
                        s.getMealScheduleDate(),
                        s.getMealScheduleTime(),
                        s.isStatus(),
                        s.getMeal().getId()
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responseList));
    }

    @Operation(summary = "특정 날짜 식사 일정 조회",description = """
            특정 날짜의 MealSchedule2 목록을 조회합니다.
            반환 항목 : 스케줄 ID, 날짜, 시간, 상태, 연관된 Meal ID
        """
    )
    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<List<MealSchedule2Response>>>getMealSchedulesByExactDate(
            Authentication authentication){
        List<MealSchedule2> schedules=mealSchedule2Service.getMealSchedulesByUserId(authentication.getName());

        List<MealSchedule2Response> responseList = schedules.stream()
                .map(s -> new MealSchedule2Response(
                        s.getId(),
                        s.getMealScheduleDate(),
                        s.getMealScheduleTime(),
                        s.isStatus(),
                        s.getMeal().getId()
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responseList));
    }

    @Operation(summary = "기간 내의 식사 일정 조회",description = """
            startDate와 endDate 사이의 MealSchedule2 목록을 조회합니다.
            반환 항복 : 스케줄 ID, 날짜, 시간, 상태, 연관된 Meal ID
        """
    )
    @GetMapping("/by-term")
    public ResponseEntity<ApiResponse<List<MealSchedule2Response>>> getMealSchedulesByDate(
            Authentication authentication,
            @ModelAttribute MealScheduleDateTermRequest termRequest) {

        List<MealSchedule2Response> responseList = mealSchedule2Service
                .getMealSchedulesByDate(authentication.getName(), termRequest.startDate(), termRequest.endDate());

        return ResponseEntity.ok(ApiResponse.success(responseList));
    }

    @Operation(summary = "복수 식사 스케줄 업데이트",description = """
                    금일 식사상태들을 true/false로 업데이트 합니다.
                    """
    )
    @PatchMapping("/today/status")
    public ResponseEntity<ApiResponse<List<UpdateMealScheduleStatusesResponse>>> updateMultipleMealScheduleStatuses(
            Authentication authentication,
            @RequestBody List<UpdateMealScheduleStatusesRequest> requests){

        List<MealSchedule2> todaySchedules=mealSchedule2Service.getMealSchedulesByUserId(authentication.getName());
        Map<Long,Boolean>requestedStatusMap=requests.stream()
                .collect(Collectors.toMap(
                        UpdateMealScheduleStatusesRequest::getScheduleId,
                        UpdateMealScheduleStatusesRequest::isStatus
                ));

        List<UpdateMealScheduleStatusesResponse> responseList = todaySchedules.stream()
                .filter(ms -> requestedStatusMap.containsKey(ms.getId()))
                .map(ms -> {
                    boolean newStatus = requestedStatusMap.get(ms.getId());
                    mealSchedule2Service.updateMealScheduleStatus(ms.getId(), newStatus);
                    return new UpdateMealScheduleStatusesResponse(
                            ms.getId(),
                            "스케줄 상태가 성공적으로 업데이트되었습니다."
                    );
                })
                .toList();


        return ResponseEntity.ok(ApiResponse.success(responseList));
    }

    @Operation(summary = "단일 식사 스케줄 상태 업데이트",description = """
            스케줄 ID로 특정 식사 스케줄의 상태를 true/false로 업데이트합니다.
            """
    )
    @PatchMapping("/{scheduleId}/status")
    public ResponseEntity<ApiResponse<String>> updateSingleMealScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestBody UpdateMealScheduleStatusRequest request) {

        mealSchedule2Service.updateMealScheduleStatus(scheduleId, request.isStatus());
        return ResponseEntity.ok(ApiResponse.success("식사 스케줄 상태가 업데이트되었습니다."));
    }

}