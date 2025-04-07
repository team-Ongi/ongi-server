package com.solution.Ongi.domain.meal.controller;

import com.solution.Ongi.domain.meal.dto.CreateMealRequest;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @PostMapping("/post/{user_id}")
    public ResponseEntity<Long> createMeal(@PathVariable("user_id") Long user_id, @RequestBody CreateMealRequest createMealRequest) {
        Meal meal = mealService.createMeal(user_id, createMealRequest);
        return ResponseEntity.ok(user_id);
    }
    //TODO: 확장성 위해 PathVariable -> RequestParam 바꾸자

    @GetMapping("/get/{user_id}")
    public ResponseEntity<List<Meal>> getAllMeals(@PathVariable("user_id") Long user_id) {
        List<Meal> meals = mealService.getAllMeals(user_id);
        return ResponseEntity.ok(meals);
    }

    // Meal 삭제 엔드포인트
    @DeleteMapping("/delete/{meal_id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long meal_id) {
        mealService.deleteMeal(meal_id);
        return ResponseEntity.noContent().build();
    }
}
