package com.solution.Ongi.domain.user.service;

import com.solution.Ongi.domain.meal.dto.MealResponse;
import com.solution.Ongi.domain.meal.dto.MealScheduleResponse;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.medication.dto.MedicationResponse;
import com.solution.Ongi.domain.medication.dto.MedicationScheduleResponse;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.dto.UserScheduleOnDateResponse;
import com.solution.Ongi.domain.user.dto.UserScheduleRangeResponse;
import com.solution.Ongi.domain.user.dto.UserSchedulesResponse;
import com.solution.Ongi.domain.user.dto.UserTodayScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final UserService userService;
    private final MealRepository mealRepository;
    private final MedicationRepository medicationRepository;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MealScheduleRepository mealScheduleRepository;

    // 유저의 Medication && Meal schedule 전체 조회
    public UserSchedulesResponse getAllSchedules(String loginId){
        User user = userService.getUserByLoginIdOrThrow(loginId);

        List<MedicationResponse> userMedicationScheduleList = getMedicationSchedules(user.getId());
        List<MealResponse> userMealScheduleList = getMealSchedules(user.getId());

        return new UserSchedulesResponse(userMedicationScheduleList,userMealScheduleList);
    }

    // 유저의 오늘 스케줄 조회
    public UserTodayScheduleResponse getTodaySchedule(String loginId, LocalDate today){
        User user = userService.getUserByLoginIdOrThrow(loginId);

        List<MedicationScheduleResponse> userMedicationScheduleList = getMedicationSchedulesExactDate(user.getId(),today);
        List<MealScheduleResponse> userMealScheduleList = getMealSchedulesByExactDate(user.getId(),today);

        return new UserTodayScheduleResponse(userMedicationScheduleList, userMealScheduleList);
    }

    // 유저의 특정 기간(각 달) 스케줄을 조회해서 복용하지 않은 날짜나 식사를 하지 않은 날짜 리스트 조회
    public UserScheduleRangeResponse getSchedulesByRange(String loginId, LocalDate startDate){
        User user = userService.getUserByLoginIdOrThrow(loginId);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<String> notTakenMedicationDates = getMedicationSchedulesByDateRange(user.getId(), startDate,endDate);
        List<String> notTakenMealDates = getMealSchedulesByDateRange(user.getId(),startDate,endDate);

        return new UserScheduleRangeResponse(notTakenMedicationDates,notTakenMealDates);
    }

    // 유저의 특정 날짜 스케줄 조회
    public UserScheduleOnDateResponse getSchedulesByDate(String loginId, LocalDate startDate){
        User user = userService.getUserByLoginIdOrThrow(loginId);

        List<MedicationScheduleResponse> userMedicationScheduleList = getMedicationSchedulesExactDate(user.getId(), startDate);
        List<MealScheduleResponse> userMealScheduleList = getMealSchedulesByExactDate(user.getId(), startDate);

        return new UserScheduleOnDateResponse(userMedicationScheduleList,userMealScheduleList);
    }

    // 유저가 해당 달에서 약을 먹지 않은 날짜 리스트 조회
    private List<String> getMedicationSchedulesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return medicationScheduleRepository
                .getNotTakenStatsByDateRange(userId, startDate,endDate)
                .stream()
                .filter(medicationSchedule -> medicationSchedule.getNotTakenCount() >0 )
                .map(medicationSchedule -> medicationSchedule.getDate().toString())
                .toList();
    }

    // 유저가 해당 달에서 식사를 하지 않은 날짜 리스트 조회
    private List<String> getMealSchedulesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealScheduleRepository
                .getNotTakenStatusByDateRange(userId, startDate, endDate)
                .stream()
                .filter(mealSchedule -> !mealSchedule.getStatus())
                .map(mealSchedule-> mealSchedule.getDate().toString())
                .toList();
    }

    // 특정 날짜 medication schedule 조회
    private List<MedicationScheduleResponse> getMedicationSchedulesExactDate(Long userId, LocalDate date) {
        return medicationScheduleRepository
                .findByUserAndDate(userId, date)
                .stream()
                .map(MedicationScheduleResponse::from)
                .toList();
    }

    //특정 날짜 meal schedule 조회
    private List<MealScheduleResponse> getMealSchedulesByExactDate(Long userId, LocalDate date){
        return mealScheduleRepository
                .findByMeal_User_IdAndScheduledDate(userId,date)
                .stream()
                .map(MealScheduleResponse::from)
                .toList();
    }

    // 전체 meal schedule 조회
    private List<MealResponse> getMealSchedules(Long userId){
        return mealRepository
                .findAllByUserId(userId).stream()
                .map(MealResponse::from)
                .toList();
    }

    // 전체 medication schedule 조회
    private List<MedicationResponse> getMedicationSchedules(Long userId){
        return medicationRepository
                .findAllByUserId(userId)
                .stream()
                .map(medication ->
                        MedicationResponse.from(
                                medication,
                                medication.getMedicationTimes(),
                                medication.getMealTypes()
                        )
                )
                .toList();
    }
}
