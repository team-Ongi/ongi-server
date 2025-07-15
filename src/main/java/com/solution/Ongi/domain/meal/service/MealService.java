package com.solution.Ongi.domain.meal.service;

import com.solution.Ongi.domain.meal.dto.CreateMealRequest;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
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
    private final UserService userService;
    private final MealRepository mealRepository;
    private final DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");
    private final MealScheduleService mealScheduleService;

    //Meal 생성
    public Meal createMeal(String loginId, CreateMealRequest createMealRequest){
        User user=userService.getUserByLoginIdOrThrow(loginId);

        Meal meal=Meal.builder()
                .mealType(MealType.valueOf(createMealRequest.getMeal_type().toUpperCase()))
                .mealTime(LocalTime.parse(createMealRequest.getMeal_time(), timeFormatter))
                .user(user)
                .build();

        mealScheduleService.createMealSchedule(meal);

        return mealRepository.save(meal);
    }

    //Meal 삭제
    public void deleteMeal(String loginId, Long mealId){
        Meal meal= getAuthorizedMeal(loginId, mealId);
        mealRepository.delete(meal);
    }

    private Meal getAuthorizedMeal(String loginId, Long mealId) {
        User user = userService.getUserByLoginIdOrThrow(loginId);

        Meal meal=mealRepository.findById(mealId)
                .orElseThrow(()->new GeneralException(ErrorStatus.MEAL_NOT_FOUND));

        if (!meal.getUser().getId().equals(user.getId())){
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        return meal;
    }
}
