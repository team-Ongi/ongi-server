package com.solution.Ongi.domain.meal.controller;

import com.solution.Ongi.domain.meal.dto.CreateMealRequest;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.dto.CreateMealResponse;
import com.solution.Ongi.domain.meal.dto.MealResponse;
import com.solution.Ongi.domain.meal.service.MealService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

//    @PostMapping("/post/{userId}/meals")
    @PostMapping("/new-meal")
    @Operation(summary = "식사 정보 등록")
    public ResponseEntity<CreateMealResponse> createMeal(
//            @PathVariable("userId") Long userId,
            Authentication authentication,
            @RequestBody CreateMealRequest request) {

        Meal meal = mealService.createMeal(authentication.getName(), request);

        //location 헤더 리소스
        URI location= URI.create("/users/"+authentication.getName()+"/meals/"+meal.getId());

        return ResponseEntity
                .created(location)
                .body(new CreateMealResponse(meal.getId(),"식사가 등록되었습니다."));
    }

    @GetMapping("/all")
    @Operation(summary = "사용자의 모든 식사 정보 조회")
    public ResponseEntity<ApiResponse<List<MealResponse>>> getAllMeals(
//            @PathVariable Long userId
            Authentication authentication){
        List<Meal> meals=mealService.getAllMeals(authentication.getName());
        List<MealResponse> responseList=meals.stream()
                .map(MealResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responseList));
    }

    //Meal 삭제 엔드포인트
    @DeleteMapping("/delete/{mealId}")
    @Operation(summary = "식사 정보 삭제")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long mealId) {
        mealService.deleteMeal(mealId);
        return ResponseEntity.noContent().build();//204 no
    }
}
