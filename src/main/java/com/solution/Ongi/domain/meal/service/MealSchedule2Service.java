package com.solution.Ongi.domain.meal.service;


import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.MealSchedule2;
import com.solution.Ongi.domain.meal.dto.MealSchedule2Response;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusesRequest;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.meal.repository.MealSchedule2Repository;
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
public class MealSchedule2Service {

    private final MealSchedule2Repository mealSchedule2Repository;
    private final MealRepository mealRepository;
    private final UserService userService;

    public MealSchedule2 createMealSchedule2(Meal meal){
        MealSchedule2 schedule=MealSchedule2.builder()
                .meal(meal)
                .mealScheduleTime(meal.getMeal_time())
                .mealScheduleDate(LocalDate.now())
                .status(false)
                .build();

        return mealSchedule2Repository.save(schedule);
    }

    //ver2
    @Scheduled(cron="0 0 0 * * *")
    public void createDailyMealSchedule(){
        mealRepository.findAll()
                .forEach(this::createMealSchedule2);
    }
    //단일 schedule 상태 업데이트
    public void updateMealScheduleStatus(Long scheduleId,boolean newStatus){
        MealSchedule2 mealSchedule=mealSchedule2Repository.findById(scheduleId)
                .orElseThrow(()->new RuntimeException("스케줄 ID " + scheduleId + "를 찾을 수 없습니다."));

        mealSchedule.setStatus(newStatus);
        mealSchedule2Repository.save(mealSchedule);
    }

    //사용자 meal schedules 상태 업데이트
    public void updateMealSchedules(Long userId, List<UpdateMealScheduleStatusesRequest> requests){
        for(UpdateMealScheduleStatusesRequest rq:requests){
            updateMealScheduleStatus(rq.getScheduleId(),rq.isStatus());
        }
    }

    //오늘 meal schedule 가져오기
    //날짜에 대응하는 MealSchedule 조회
//    public List<MealSchedule2> getMealSchedulesByUserId(Long userId){
//        userService.getUserByIdOrThrow(userId);
//        return mealSchedule2Repository.findByMeal_User_IdAndMealScheduleDate(userId,LocalDate.now());
//    }
    public List<MealSchedule2> getMealSchedulesByUserId(String loginId){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        return mealSchedule2Repository.findByMeal_User_IdAndMealScheduleDate(user.getId(),LocalDate.now());
    }

    //특정 날짜 meal schedule 조회
    public List<MealSchedule2> getMealSchedulesByExactDate(Long userId,LocalDate date){
        userService.getUserByIdOrThrow(userId);
        return mealSchedule2Repository.findByMeal_User_IdAndMealScheduleDate(userId,date);
    }

    //생성 시점이 특정 기한 내에 속하는 meal schedule 조회
    public List<MealSchedule2Response> getMealSchedulesByDate(String loginId,LocalDate startDate,LocalDate endDate){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        return mealSchedule2Repository
                .findByMeal_User_IdAndMealScheduleDateBetween(user.getId(), startDate, endDate)
                .stream()
                .map(MealSchedule2Response::from)
                .toList();
    }
}
