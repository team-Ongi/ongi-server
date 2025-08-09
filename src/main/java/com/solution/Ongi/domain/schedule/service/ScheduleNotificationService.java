package com.solution.Ongi.domain.schedule.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.dto.UpdateMealScheduleStatusRequest;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.meal.service.MealScheduleService;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationStatusRequest;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.medication.service.MedicationScheduleService;
import com.solution.Ongi.domain.push.PushNotificationService;
import com.solution.Ongi.domain.schedule.dto.DenyRequest;
import com.solution.Ongi.domain.schedule.dto.UpcomingScheduleResponse;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.infra.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleNotificationService {

    private final MealScheduleRepository mealScheduleRepository;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final UserService userService;
    private final MealScheduleService mealScheduleService;
    private final MedicationScheduleService medicationScheduleService;

    private final PushNotificationService pushNotificationService;
    private final SubscriptionService subscriptionService;

    // 임박한 스케줄 조회
    // 직전 스케줄 status 가 false 일 시 currentIgnoreCnt +1
    // currentIgnoreCnt==maxIgnoreCnt 일 시 fcm 알림
    @Transactional(readOnly = true)
    public UpcomingScheduleResponse getNext(String loginId){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        LocalDate today=LocalDate.now();
        LocalTime currentTime=LocalTime.now();

        // 현재 시간 기준 임박한 meal, med 조회
        Optional<MealSchedule> nextMeal=mealScheduleRepository
                .findFirstByMeal_User_IdAndScheduledDateAndStatusFalseAndScheduledTimeAfterOrderByScheduledTimeAsc(
                        user.getId(),today,currentTime
                );
        Optional<MedicationSchedule> nextMed=medicationScheduleRepository
                .findFirstByMedication_User_IdAndScheduledDateAndStatusFalseAndScheduledTimeAfterOrderByScheduledTimeAsc(
                        user.getId(),today,currentTime
                );

        // 남은 스케줄 없을 시
        if(nextMeal.isEmpty()&&nextMed.isEmpty()){
            return null;
        }

        // meal, med 둘 다 존재 시 더 임박한 것
        if(nextMeal.isPresent()&&nextMed.isPresent()){
            MealSchedule meal=nextMeal.get();
            MedicationSchedule med=nextMed.get();
            return meal.getScheduledTime().isBefore(med.getScheduledTime())
                    ? mapMeal(meal)
                    : mapMed(med);
        }

        // 둘 중 하나만 존재 시
        return nextMeal
                .map(this::mapMeal)
                .orElseGet(()->mapMed(nextMed.get()));
    }

    // '완료' 처리후 다음 스케줄 반환
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
        // 다음 임박한 스케줄 반환
        return getNext(loginId);
    }

    // '다음에' 처리후 다음 스케줄 조회
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

//        //CurrentIgnoreCount +1
//        Long currentIgnore=userService.addUserCurrentIgnoreCount(loginId);
//        Long maxIgnore=user.getIgnoreCnt().longValue();
//
//        //if currentIgnore==maxIgnore send FCM push
//        if(currentIgnore.equals(maxIgnore)){
//            try{
//                String token=subscriptionService.getTokenForUser(user.getId());
//                pushNotificationService.sendNotification(
//                        token,
//                        "긴급 상황 알림 발생",
//                        "현재 알람 거절 횟수("+currentIgnore+"회)가 최대 허용 횟수에 도달했습니다."
//                );
//            }catch (FirebaseMessagingException e){
//                //TODO: 푸시 알람 실패 처리
//            }
//        }

        return getNext(loginId);

    }

    private UpcomingScheduleResponse mapMeal(MealSchedule mealSchedule){
        return new UpcomingScheduleResponse(mealSchedule);
    }

    private UpcomingScheduleResponse mapMed(MedicationSchedule medicationSchedule){
        return new UpcomingScheduleResponse(medicationSchedule);
    }
}
