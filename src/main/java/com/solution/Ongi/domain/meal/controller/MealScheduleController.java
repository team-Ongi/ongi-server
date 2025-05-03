package com.solution.Ongi.domain.meal.controller;

import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.dto.MealScheduleStatusUpdateRequest;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.meal.service.MealScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meal-schedule")
@RequiredArgsConstructor
public class MealScheduleController {
    private final MealScheduleService mealScheduleService;
    private final MealScheduleRepository mealScheduleRepository;

    @GetMapping("/users/{userId}/meal-schedules")
    public ResponseEntity<List<MealSchedule>> getUserMealSchedules(@PathVariable Long userId){
        List<MealSchedule> mealSchedules=mealScheduleService.getMealSchedulesByUserId(userId);
        return ResponseEntity.ok(mealSchedules);
    }

    @PatchMapping("/users/{userId}/meal-schedules/status")
    public ResponseEntity<String> updateMultipleMealScheduleStatuses(
            @PathVariable Long userId,
            @RequestBody List<MealScheduleStatusUpdateRequest> requests){

        mealScheduleService.updateMealSchedules(userId, requests);
        return ResponseEntity.ok("사용자의 스케줄 상태가 모두 업데이트 되었습니다.");
    }

}
