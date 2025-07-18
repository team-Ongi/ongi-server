package com.solution.Ongi.domain.user.service;

import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.dto.MealResponse;
import com.solution.Ongi.domain.meal.dto.MealScheduleResponse;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.dto.MedicationResponse;
import com.solution.Ongi.domain.medication.dto.MedicationScheduleResponse;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.dto.UserScheduleRangeResponse;
import com.solution.Ongi.domain.user.dto.UserSchedulesResponse;
import com.solution.Ongi.domain.user.dto.UserTodayScheduleResponse;
import com.solution.Ongi.domain.user.repository.projection.NotTakenMealStatusProjection;
import com.solution.Ongi.domain.user.repository.projection.NotTakenMedicationStatusProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final UserService userService;
    private final MealRepository mealRepository;
    private final MedicationRepository medicationRepository;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MealScheduleRepository mealScheduleRepository;

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

    // 유저의 오늘 스케줄 조회
    public UserTodayScheduleResponse getTodaySchedule(String loginId, LocalDate today){
        List<MedicationScheduleResponse> userMedicationScheduleList = getUserMedicationSchedulesExactDate(loginId,today);
        List<MealScheduleResponse> userMealScheduleList = getMealSchedulesByExactDate(loginId,today);
        return new UserTodayScheduleResponse(userMedicationScheduleList, userMealScheduleList);
    }

    // 유저의 특정 날짜 스케줄 조회
    public UserScheduleRangeResponse getUserSchedulesByDateRange(String loginId, LocalDate startDate){
        User user = userService.getUserByLoginIdOrThrow(loginId);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<String> notTakenMedicationDates = getUserMedicationSchedulesByDateRange(user, startDate,endDate);
        List<String> notTakenMealDates = getUserMealSchedulesByDateRange(user,startDate,endDate);
        return new UserScheduleRangeResponse(notTakenMedicationDates,notTakenMealDates);
    }

    //생성 시점이 특정 기한 내에 속하는 meal schedule 조회
    private List<MealScheduleResponse> getMealSchedulesByDate(String loginId, LocalDate startDate, LocalDate endDate){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        return mealScheduleRepository
                .findByMeal_User_IdAndMealScheduleDateBetween(user.getId(), startDate, endDate)
                .stream()
                .map(MealScheduleResponse::from)
                .toList();
    }

    // 유저가 해당 달에서 약을 먹지 않은 날짜 리스트 조회
    private List<String> getUserMedicationSchedulesByDateRange(User user, LocalDate startDate, LocalDate endDate) {
        List<NotTakenMedicationStatusProjection> notTakenStatsByDateRange = medicationScheduleRepository.getNotTakenStatsByDateRange(user.getId(), startDate,endDate);
        List<String> notTakenMedicationDates = new ArrayList<>();

        for(NotTakenMedicationStatusProjection data : notTakenStatsByDateRange){
            if(data.getNotTakenCount() >0 )
                notTakenMedicationDates.add(data.getDate().toString());
        }
        return notTakenMedicationDates;
    }

    // 유저가 해당 달에서 식사를 하지 않은 날짜 리스트 조회
    private List<String> getUserMealSchedulesByDateRange(User user, LocalDate startDate, LocalDate endDate) {
        List<NotTakenMealStatusProjection> notTakenStatsByDateRange = mealScheduleRepository.getNotTakenStatusByDateRange(user.getId(), startDate,endDate);
        List<String> notTakenMedicationDates = new ArrayList<>();

        for(NotTakenMealStatusProjection data : notTakenStatsByDateRange){
            if(!data.getStatus())
                notTakenMedicationDates.add(data.getDate().toString());
        }
        return notTakenMedicationDates;
    }

    // 특정 날짜 medication schedule 조회
    private List<MedicationScheduleResponse> getUserMedicationSchedulesExactDate(String loginId, LocalDate date) {
        User user = userService.getUserByLoginIdOrThrow(loginId);
        List<MedicationSchedule> schedules = medicationScheduleRepository.findByUserAndDate(user.getId(), date);

        return (schedules.stream()
                .map(MedicationScheduleResponse::from)
                .toList());
    }

    //특정 날짜 meal schedule 조회
    private List<MealScheduleResponse> getMealSchedulesByExactDate(String loginId, LocalDate date){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        return mealScheduleRepository
                .findByMeal_User_IdAndMealScheduleDate(user.getId(),date)
                .stream()
                .map(MealScheduleResponse::from)
                .toList();
    }
}
