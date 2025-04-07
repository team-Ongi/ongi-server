package com.solution.Ongi.domain.meal.service;

import com.solution.Ongi.domain.meal.dto.CreateMealRequest;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.enums.MealType;
import com.solution.Ongi.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MealService {
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");

    //Meal 생성
    public Meal createMeal(Long user_id, CreateMealRequest createMealRequest){
        User user=userRepository.findById(user_id)
                .orElseThrow(()-> new RuntimeException("사용자가 존재하지 않습니다."));

        Meal meal=Meal.builder()
                .meal_type(MealType.valueOf(createMealRequest.getMeal_type().toUpperCase()))
                .meal_time(LocalTime.parse(createMealRequest.getMeal_time(), timeFormatter))
                .user(user)
                .build();

        return mealRepository.save(meal);
    }

    //TODO: Exception 처리

    //유저의 Meal 전체 조회
    public List<Meal> getAllMeals(Long user_id){
        userRepository.findById(user_id)
                .orElseThrow(()->new RuntimeException("사용자가 존재하지 않습니다."));
        return mealRepository.findByUserId(user_id);
    }

    //Meal 삭제
    public void deleteMeal(Long meal_id){
        Meal meal=mealRepository.findById(meal_id)
                .orElseThrow(()->new RuntimeException("식사가 존재하지 않습니다."));
        mealRepository.delete(meal);
    }
}
