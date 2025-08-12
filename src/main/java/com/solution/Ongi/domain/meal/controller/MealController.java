package com.solution.Ongi.domain.meal.controller;

import com.solution.Ongi.domain.meal.dto.CreateMealRequest;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.dto.CreateMealResponse;
import com.solution.Ongi.domain.meal.dto.UpdateMealRequest;
import com.solution.Ongi.domain.meal.service.MealService;
import com.solution.Ongi.global.response.ApiResponse;
import com.solution.Ongi.global.response.code.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<String>> createMeal(
            Authentication authentication,
            @RequestBody CreateMealRequest request) {

        mealService.createMeal(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("성공", SuccessStatus.SUCCESS_200));
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

    @PutMapping("/meal/{mealId}")
    @Operation(summary = "식사 정보 수정",
            description = """
                    식사 정보를 수정합니다. <br><br>
                    - `mealType` : 수정할 끼니 정보 <br>
                    - `mealTime` : 수정할 끼니 시간 <br>
                    예시:
                    ```
                    {
                      "mealType": "BREAKFAST",
                      "mealTime": "07:00",
                    }
                    ```
                    """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "식전/식후 복용 약 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않거나 약 정보가 존재하지 않음", content = @Content(mediaType = "application/json", schema = @Schema()))
    public ResponseEntity<ApiResponse<String>> updateMeal(
            Authentication authentication,
            @PathVariable Long mealId,
            @RequestBody UpdateMealRequest request){

        mealService.updateMeal(authentication.getName(), mealId,request);
        return ResponseEntity.noContent().build();

    }
}
