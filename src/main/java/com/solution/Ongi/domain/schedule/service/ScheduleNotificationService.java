package com.solution.Ongi.domain.schedule.service;

import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusRequest;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.meal.service.MealScheduleService;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationStatusRequest;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.medication.service.MedicationScheduleService;
import com.solution.Ongi.domain.push.service.DeviceTokenService;
import com.solution.Ongi.domain.push.service.PushNotificationService;
import com.solution.Ongi.domain.schedule.dto.DenyRequest;
import com.solution.Ongi.domain.schedule.dto.UpcomingScheduleResponse;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.infra.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleNotificationService {

    private final MealScheduleRepository mealScheduleRepository;
    private final MedicationScheduleRepository medicationScheduleRepository;

    private final UserService userService;
    private final MealScheduleService mealScheduleService;
    private final MedicationScheduleService medicationScheduleService;
    private final MissedScheduleService missedScheduleService;

    private final DeviceTokenService deviceTokenService;
    private final PushNotificationService pushNotificationService;


    // 임박한 스케줄 조회
    // 직전 스케줄 status 가 false 일 시 missedSchedule 로 체크 (currentIgnoreCnt+1 로직 포함)
    // currentIgnoreCnt==maxIgnoreCnt 일 시 fcm 알림
    @Transactional
    public UpcomingScheduleResponse getNext(String loginId){

        User user=userService.getUserByLoginIdOrThrow(loginId);

        LocalDate today=LocalDate.now();
        LocalTime currentTime=LocalTime.now();

        //1. 직전 스케줄 status false 일 시 missedSchedule로 체크
        missedScheduleService.detectAndRecordMostRecentMissed(loginId);

        //2. currentIgnoreCnt == ignoreCnt 일 시 푸시 알림 전송, currentIgnoreCnt=0 으로 초기화
        if(user.getIgnoreCnt()<=user.getCurrentIgnoreCnt()){
            sendNotifyAndClearanceToken(user);
            userService.setUserCurrentIgnoreCnt(user.getLoginId(), 0);
        }
        log.info("현재 시간: " + today + currentTime);

        //3. 현재 시간 기준 다음 임박한 스케줄 조회
        Optional<MealSchedule> nextMeal=mealScheduleRepository
                .findFirstByMeal_User_IdAndScheduledDateAndStatusFalseAndScheduledTimeAfterOrderByScheduledTimeAsc(
                        user.getId(),today,currentTime
                );
        Optional<MedicationSchedule> nextMed=medicationScheduleRepository
                .findFirstByMedication_User_IdAndScheduledDateAndStatusFalseAndScheduledTimeAfterOrderByScheduledTimeAsc(
                        user.getId(),today,currentTime
                );

        //4.1. 남은 스케줄 없을 시 return null
        if(nextMeal.isEmpty()&&nextMed.isEmpty()){
            return null;
        }

        //4.2. meal, med 둘 다 존재 시 더 임박한 것 return
        if(nextMeal.isPresent()&&nextMed.isPresent()){
            MealSchedule meal=nextMeal.get();
            MedicationSchedule med=nextMed.get();
            return meal.getScheduledTime().isBefore(med.getScheduledTime())
                    ? mapMeal(meal)
                    : mapMed(med);
        }

        //4.3. 둘 중 하나만 존재 시 해당 스케줄 return
        return nextMeal
                .map(this::mapMeal)
                .orElseGet(()->mapMed(nextMed.get()));
    }

    //5.1. '완료' 처리후 다음 스케줄 반환
    @Transactional
    public UpcomingScheduleResponse confirmAndGetNext(String loginId){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        // 임박한 스케줄 조회
        UpcomingScheduleResponse current=getNext(loginId);
        // 완료 처리
        if("MEAL".equals(current.type())){
            mealScheduleService.updateMealScheduleStatus(loginId,current.scheduleId(),
                    new UpdateMealScheduleStatusRequest(true,null));
        }
        else if("MEDICATION".equals(current.type())){
            medicationScheduleService.updateIsTaken(loginId, current.scheduleId(),
                    new UpdateMedicationStatusRequest(true,null));
        }
        else{
            medicationScheduleService.updateIsTaken(loginId, current.scheduleId(),
                    new UpdateMedicationStatusRequest(true,null));
        }
        return getNext(loginId);
    }

    //5.2. '다음에' 처리후 다음 스케줄 조회
    @Transactional
    public UpcomingScheduleResponse denyAndGetNext(String loginId, DenyRequest request){

        User user=userService.getUserByLoginIdOrThrow(loginId);
        UpcomingScheduleResponse current=getNext(loginId);
        String reason= request.reason();

        // 상태 업데이트
        if("MEAL".equals(current.type())){
            mealScheduleService.updateMealScheduleStatus(loginId,current.scheduleId(),
                    new UpdateMealScheduleStatusRequest(false,reason));
        }
        else{
            medicationScheduleService.updateIsTaken(loginId, current.scheduleId(),
                    new UpdateMedicationStatusRequest(false,reason));
        }
        return getNext(loginId);
    }

    private UpcomingScheduleResponse mapMeal(MealSchedule mealSchedule){
        return new UpcomingScheduleResponse(mealSchedule);
    }

    private UpcomingScheduleResponse mapMed(MedicationSchedule medicationSchedule){
        return new UpcomingScheduleResponse(medicationSchedule);
    }

    private void sendNotifyAndClearanceToken(User account){
        try {
            var tokens=deviceTokenService.getActiveTokenByUserId(account.getId());
            if(tokens.isEmpty())return;

            String title= "알림 무시 최대횟수 도달";
            String body= "알림 무시 최대 횟수에 도달했습니다. 보호자분의 확인이 필요합니다. ";

            var invalid=pushNotificationService.sendToTokens(tokens, title, body);

            if(!invalid.isEmpty()){
                deviceTokenService.deactivateAllByToken(invalid);
            }
        } catch (Exception e){
            log.warn("임계치 푸시 실패 userId={}, err={}", account.getId(), e.getMessage());
        }
    }

}
