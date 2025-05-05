package com.solution.Ongi.domain.meal.service;


import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusesRequest;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * create -> Meal 생성 시점에 동시에 생성 (all null로)-> cascade 로 커버쳐지나? o
 * 주기적 초기화 (update_meal status 만 false 로 초기화)   o
 * 오늘 날짜에 해당하는 MealSchedule 조회 서비스 (get)(메인 화면 서비스) -> 걍 띄우면 되네~ 개이득  o
 * 상태 체크 (update), count ++ -> user 멤버로 넣는게 나을듯?
 * 누락 횟수 서비스 (user service)
 * 오늘 복약 처리 변경
*/

@Service
@Transactional
@RequiredArgsConstructor
public class MealScheduleService {

    private final MealScheduleRepository mealScheduleRepository;
    private final UserService userService;

    public MealSchedule createMealSchedule(Meal meal){
        MealSchedule schedule=MealSchedule.builder()
                .meal(meal)
                .meal_schedule_time(meal.getMeal_time())
                .status(false)
                .build();

        return mealScheduleRepository.save(schedule);
    }

    //단일 schedule 상태 업데이트
    public void updateMealScheduleStatus(Long scheduleId,boolean newStatus){
        MealSchedule mealSchedule=mealScheduleRepository.findById(scheduleId)
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

    //매일 자정 status 초기화
    @Scheduled(cron = "0 0 0 * * *")
    public void resetMealScheduleStatus(){
        List<MealSchedule>mealSchedules=mealScheduleRepository.findAll();
        for(MealSchedule mealSchedule:mealSchedules){
            mealSchedule.setStatus(false);
        }
        mealScheduleRepository.saveAll(mealSchedules);
    }

    //오늘 meal schedule 가져오기
    public List<MealSchedule> getMealSchedulesByUserId(Long userId){
        userService.getUserByIdOrThrow(userId);
        return mealScheduleRepository.findByMeal_User_Id(userId);
    }
}
