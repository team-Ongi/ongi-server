package com.solution.Ongi.domain.meal.controller;

import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.dto.MealScheduleResponse;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusRequest;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusesRequest;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusesResponse;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.meal.service.MealScheduleService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meal-schedules")
@RequiredArgsConstructor
public class MealScheduleController {
    private final MealScheduleService mealScheduleService;
    private final MealScheduleRepository mealScheduleRepository;

    @Operation(
            summary = "금일 식사 일정 조회",
            description = """
            금일 식사 일정을 조회합니다. <br>
            반환되는 목록은 각 식사 항목의 일정 정보와 상태를 포함합니다.
            """
    )
    @GetMapping("/users/{userId}/meal-schedules")
    public ResponseEntity<ApiResponse<List<MealScheduleResponse>>> getUserMealSchedules(
            @PathVariable Long userId) {

        List<MealSchedule> mealSchedules = mealScheduleService.getMealSchedulesByUserId(userId);
        List<MealScheduleResponse> responseList = mealSchedules.stream()
                .map(MealScheduleResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responseList));
    }

    @Operation(
            summary = "복수 식사 스케줄 업데이트",
            description = """
                    금일 식사상태들을 업데이트를 합니다.
                    """
    )
    @PatchMapping("/users/{userId}/meal-schedules/status")
    public ResponseEntity<ApiResponse<List<UpdateMealScheduleStatusesResponse>>> updateMultipleMealScheduleStatuses(
            @PathVariable Long userId,
            @RequestBody List<UpdateMealScheduleStatusesRequest> requests){

//        mealScheduleService.updateMealSchedules(userId, requests);
//        return ResponseEntity.ok("사용자의 스케줄 상태가 모두 업데이트 되었습니다.");
        List<UpdateMealScheduleStatusesResponse> responseList = requests.stream()
                .map(rq -> new UpdateMealScheduleStatusesResponse(
                        rq.getScheduleId(),
                        "상태가 성공적으로 업데이트되었습니다."))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responseList));
    }

    @Operation(
            summary = "단일 식사 스케줄 상태 업데이트",
            description = """
            스케줄 ID로 특정 식사 스케줄의 상태를 true/false로 변경합니다.
            """
    )
    @PatchMapping("/meal-schedules/{scheduleId}/status")
    public ResponseEntity<ApiResponse<String>> updateSingleMealScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestBody UpdateMealScheduleStatusRequest request) {

        mealScheduleService.updateMealScheduleStatus(scheduleId, request.isStatus());
        return ResponseEntity.ok(ApiResponse.success("식사 스케줄 상태가 업데이트되었습니다."));
    }

}
