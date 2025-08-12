package com.solution.Ongi.domain.meal.service;


import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusRequest;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusResponse;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MealScheduleService {

    private final MealScheduleRepository mealScheduleRepository;
    private final MealRepository mealRepository;
    private final UserService userService;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public void createMealSchedule(Meal meal){
        MealSchedule schedule= MealSchedule.builder()
                .meal(meal)
                .scheduledTime(meal.getMealTime())
                .scheduledDate(LocalDate.now(KST))
                .status(false)
                .build();

        mealScheduleRepository.save(schedule);
    }

    //매일 자정 meal schedule 생성
    @Scheduled(cron="0 0 0 * * *")
    public void createDailyMealSchedule(){
        mealRepository.findAll()
                .forEach(this::createMealSchedule);
    }

    //단일 schedule 상태 업데이트
    public UpdateMealScheduleStatusResponse updateMealScheduleStatus(String loginId, Long scheduleId, UpdateMealScheduleStatusRequest request){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        MealSchedule mealSchedule= mealScheduleRepository.findById(scheduleId)
                .orElseThrow(()->new RuntimeException("스케줄 ID " + scheduleId + "를 찾을 수 없습니다."));

        if(!mealSchedule.getMeal().getUser().getLoginId().equals(loginId)){
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }
        if(request.status()){
            mealSchedule.markAsTaken();
        }else{
            mealSchedule.markAsNotTaken(request.reason());
        }

        return new UpdateMealScheduleStatusResponse(
                mealSchedule.getId(),
                mealSchedule.getStatus(),
                request.status()?"식사 완료로 업데이트":"식사 누락으로 업데이트"
        );
    }

    //날짜에 대응하는 MealSchedule 조회
    public List<MealSchedule> getMealSchedulesByUserId(String loginId){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        return mealScheduleRepository.findByMeal_User_IdAndScheduledDate(user.getId(),LocalDate.now());
    }
}
