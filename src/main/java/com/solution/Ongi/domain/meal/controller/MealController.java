package com.solution.Ongi.domain.meal.controller;

import com.solution.Ongi.domain.meal.dto.CreateMealRequest;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.dto.CreateMealResponse;
import com.solution.Ongi.domain.meal.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @PostMapping("/new-meal")
    @Operation(summary = "식사 정보 등록")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "식사 정보 등록 완료", content = @Content(schema = @Schema(implementation = CreateMealResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저(ID)가 존재하지 않는 경우", content = @Content)
    public ResponseEntity<CreateMealResponse> createMeal(
            Authentication authentication,
            @RequestBody CreateMealRequest request) {

        Meal meal = mealService.createMeal(authentication.getName(), request);

        //location 헤더 리소스
        URI location= URI.create("/users/"+authentication.getName()+"/meals/"+meal.getId());

        return ResponseEntity
                .created(location)
                .body(new CreateMealResponse(meal.getId(),"식사가 등록되었습니다."));
    }

    //Meal 삭제 엔드포인트
    @DeleteMapping("/{mealId}")
    @Operation(summary = "식사 정보 삭제")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "식사 정보 삭제 성공", content = @Content(mediaType = "application/json", schema = @Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "식사 정보를 삭제할 권한이 없음", content = @Content(mediaType = "application/json", schema = @Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않거나 식사 정보가 존재하지 않음", content = @Content(mediaType = "application/json", schema = @Schema()))
    public ResponseEntity<Void> deleteMeal(Authentication authentication,@PathVariable Long mealId) {
        mealService.deleteMeal(authentication.getName(),mealId);
        return ResponseEntity.noContent().build();
    }
}
