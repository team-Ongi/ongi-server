package com.solution.Ongi.domain.meal.service;


import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.MealSchedule;
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
                .scheduledTime(meal.getMealTime())
                .scheduledDate(LocalDate.now())
                .status(false)
                .build();

        return mealScheduleRepository.save(schedule);
    }

    //매일 자정 meal schedule 생성
    @Scheduled(cron="0 0 0 * * *")
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

    //날짜에 대응하는 MealSchedule 조회
    public List<MealSchedule> getMealSchedulesByUserId(String loginId){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        return mealScheduleRepository.findByMeal_User_IdAndScheduledDate(user.getId(),LocalDate.now());
    }
}
