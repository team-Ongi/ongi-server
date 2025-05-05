package com.solution.Ongi.domain.meal.service;

import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.dto.CreateMealRequest;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.enums.AlertInterval;
import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.user.enums.RelationType;
import com.solution.Ongi.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;


@SpringBootTest
@Transactional//종료 후 롤백
class MealServiceTest {

    @Autowired MealService mealService;
    @Autowired MealRepository mealRepository;
    @Autowired UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUser(){
        testUser = User.builder()
                .loginId("testUser")
                .password("testPassword")
                .guardianName("guardian")
                .guardianPhone("010-1111-2222")
                .seniorName("senior")
                .seniorAge(80)
                .seniorPhone("010-3333-4444")
                .relation(RelationType.SON)
                .alertMax(AlertInterval.MINUTES_30)
                .build();
        userRepository.save(testUser);
    }


    @Test
    void createMeal() throws Exception{
        //Given
        CreateMealRequest request= CreateMealRequest.builder()
                .meal_type("BREAKFAST")
                .meal_time("08:00")
                .build();

        //When
        Meal meal=mealService.createMeal(testUser.getId(),request);

        //Then
        Assertions.assertThat(meal.getMeal_type()).isEqualTo(MealType.BREAKFAST);
        Assertions.assertThat(meal.getMeal_time()).isEqualTo(LocalTime.of(8,0));
    }

    @Test
    void getAllMeals() {
        //Given
        Meal meal1 = Meal.builder()
                .meal_type(MealType.BREAKFAST)
                .meal_time(LocalTime.of(8, 00))
                .user(testUser)
                .build();
        mealRepository.save(meal1);

        Meal meal2 = Meal.builder()
                .meal_type(MealType.LUNCH)
                .meal_time(LocalTime.of(13, 30))
                .user(testUser)
                .build();
        mealRepository.save(meal2);

        //When
        List<Meal> meals=mealService.getAllMeals(testUser.getId());

        //Then
        Assertions.assertThat(meals).hasSize(2);
        Assertions.assertThat(meals.get(0).getMeal_type()).isEqualTo(MealType.BREAKFAST);
    }

    @Test
    void deleteMeal() {
        // Given
        Meal meal = Meal.builder()
                .meal_type(MealType.DINNER)
                .meal_time(LocalTime.of(18, 30))
                .user(testUser)
                .build();
        Meal saved = mealRepository.save(meal);

        // When
        mealService.deleteMeal(saved.getId());

        // Then
        Assertions.assertThat(mealRepository.findById(saved.getId())).isEmpty();
    }
}