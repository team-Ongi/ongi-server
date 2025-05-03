package com.solution.Ongi.domain.meal.service;


import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * create -> Meal 생성 시점에 동시에 생성 (all null로)-> cascade 로 커버쳐지나?
 * 주기적 초기화 (update_meal status 만 false 로 초기화)
 * 오늘 날짜에 해당하는 MealSchedule 조회 서비스 (get)(메인 화면 서비스)
 * 상태 체크 (update), count ++ -> user 멤버로 넣는게 나을듯?
 * 누락 횟수 서비스 (user service)
 * 오늘 복약 처리 변경
 * 가장 임박한 mealSchedule 데려오기
*/

@Service
@Transactional
@RequiredArgsConstructor
public class MealScheduleService {

    private final MealScheduleRepository mealScheduleRepository;

    public MealSchedule create(Meal meal){
        MealSchedule schedule=MealSchedule.builder()
                .meal(meal)
                .meal_schedule_time(meal.getMeal_time())
                .status(false)
                .build();

        return mealScheduleRepository.save(schedule);
    }

    public void setMealScheduleStatus(MealSchedule schedule, boolean newStatus){
        schedule.setStatus(newStatus);
        mealScheduleRepository.save(schedule);
    }

    //매일 자정 status 초기화
    @Scheduled(cron = "0 0 0 * * *")
    public void resetMealScheduleStatus(){
        List<MealSchedule> schedules=mealScheduleRepository.findAll();
        for (MealSchedule schedule:schedules){
            setMealScheduleStatus(schedule,false);
        }
    }
}
