package com.solution.Ongi.domain.meal.service;


import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.dto.MealScheduleResponse;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusesRequest;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MealScheduleService {

    private final MealScheduleRepository mealScheduleRepository;
    private final MealRepository mealRepository;
    private final UserService userService;

    public MealSchedule createMealSchedule(Meal meal){
        MealSchedule schedule= MealSchedule.builder()
                .meal(meal)
                .mealScheduleTime(meal.getMealTime())
                .mealScheduleDate(LocalDate.now())
                .status(false)
                .build();

        return mealScheduleRepository.save(schedule);
    }

    //매일 자정 meal schedule 생성
//    @Scheduled(cron="0 0 0 * * *")
    @Scheduled(cron="0 0/1 * * * ?")
    public void createDailyMealSchedule(){
        mealRepository.findAll()
                .forEach(this::createMealSchedule);
    }

    //단일 schedule 상태 업데이트
    public void updateMealScheduleStatus(Long scheduleId,boolean newStatus){
        MealSchedule mealSchedule= mealScheduleRepository.findById(scheduleId)
                .orElseThrow(()->new RuntimeException("스케줄 ID " + scheduleId + "를 찾을 수 없습니다."));

        mealSchedule.setStatus(newStatus);
        mealScheduleRepository.save(mealSchedule);
    }

    //사용자 meal schedules 상태 업데이트
    public void updateMealSchedules(Long userId, List<UpdateMealScheduleStatusesRequest> requests){
        for(UpdateMealScheduleStatusesRequest rq:requests){
            updateMealScheduleStatus(rq.getScheduleId(),rq.isStatus());
        }
    }

    //날짜에 대응하는 MealSchedule 조회
    public List<MealSchedule> getMealSchedulesByUserId(String loginId){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        return mealScheduleRepository.findByMeal_User_IdAndMealScheduleDate(user.getId(),LocalDate.now());
    }

    //특정 날짜 meal schedule 조회
    public List<MealScheduleResponse> getMealSchedulesByExactDate(String loginId, LocalDate date){
        User user=userService.getUserByLoginIdOrThrow(loginId);
    //    return mealScheduleRepository.findByMeal_User_IdAndMealScheduleDate(user.getId(),date);
        return mealScheduleRepository
                .findByMeal_User_IdAndMealScheduleDate(user.getId(),date)
                .stream()
                .map(MealScheduleResponse::from)
                .toList();
    }

    //생성 시점이 특정 기한 내에 속하는 meal schedule 조회
    public List<MealScheduleResponse> getMealSchedulesByDate(String loginId, LocalDate startDate, LocalDate endDate){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        return mealScheduleRepository
                .findByMeal_User_IdAndMealScheduleDateBetween(user.getId(), startDate, endDate)
                .stream()
                .map(MealScheduleResponse::from)
                .toList();
    }
}
