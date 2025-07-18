package com.solution.Ongi.domain.user.service;

import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.dto.MealResponse;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.dto.MedicationResponse;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.dto.UserSchedulesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final UserService userService;
    private final MealRepository mealRepository;
    private final MedicationRepository medicationRepository;

    // 유저의 Medication && Meal 전체 조회
    public UserSchedulesResponse getAllSchedules(String loginId){
        User user = userService.getUserByLoginIdOrThrow(loginId);
        List<Medication> medications = medicationRepository.findAllByUserId(user.getId());
        List<MedicationResponse> medicationResponseList = medications.stream()
                .map(medication ->
                        MedicationResponse.from(
                                medication,
                                medication.getMedicationTimes(),
                                medication.getMealTypes()
                        )
                )
                .toList();
        List<Meal> meals = mealRepository.findAllByUserId(user.getId());
        List<MealResponse> mealResponseList=meals.stream()
                .map(MealResponse::from)
                .toList();

        return new UserSchedulesResponse(medicationResponseList,mealResponseList);
    }
}
