package com.solution.Ongi.domain.meal.controller;

import com.solution.Ongi.domain.meal.dto.CreateMealRequest;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.dto.CreateMealResponse;
import com.solution.Ongi.domain.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @PostMapping("/post/{userId}/meals")
    public ResponseEntity<CreateMealResponse> createMeal(
            @PathVariable("userId") Long userId,
            @RequestBody CreateMealRequest request) {

        Meal meal = mealService.createMeal(userId, request);

        //location 헤더 리소스
        URI location= URI.create("/users/"+userId+"/meals/"+meal.getId());

        return ResponseEntity
                .created(location)
                .body(new CreateMealResponse(meal.getId(),"식사가 등록되었습니다."));
    }

    @GetMapping("/users/{userId}/meals")
    public ResponseEntity<List<Meal>> getAllMeals(@PathVariable("userId") Long user_id) {
        List<Meal> meals = mealService.getAllMeals(user_id);
        return ResponseEntity.ok(meals);
    }

    //Meal 삭제 엔드포인트
    @DeleteMapping("/delete/{mealId}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long mealId) {
        mealService.deleteMeal(mealId);
        return ResponseEntity.noContent().build();//204 no
    }
}
