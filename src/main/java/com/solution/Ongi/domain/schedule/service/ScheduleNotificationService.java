package com.solution.Ongi.domain.schedule.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.meal.service.MealScheduleService;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationStatusRequest;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.medication.service.MedicationScheduleService;
import com.solution.Ongi.domain.push.PushNotificationService;
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

    @Transactional(readOnly = true)
    public UpcomingScheduleResponse getNext(String loginId){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        LocalDate today=LocalDate.now();
        LocalTime currentTime=LocalTime.now();

        Optional<MealSchedule> nextMeal=mealScheduleRepository
                .findFirstByMeal_User_IdAndScheduledDateAndStatusFalseAndScheduledTimeAfterOrderByScheduledTimeAsc(
                        user.getId(),today,currentTime
                );
        Optional<MedicationSchedule> nextMed=medicationScheduleRepository
                .findFirstByMedication_User_IdAndScheduledDateAndStatusFalseAndScheduledTimeAfterOrderByScheduledTimeAsc(
                        user.getId(),today,currentTime
                );

        if(nextMeal.isEmpty()&&nextMed.isEmpty()){
            throw new RuntimeException("남은 스케줄이 없습니다.");
        }

        //if both exist, return closer schedule
        if(nextMeal.isPresent()&&nextMed.isPresent()){
            MealSchedule meal=nextMeal.get();
            MedicationSchedule med=nextMed.get();
            return meal.getScheduledTime().isBefore(med.getScheduledTime())
                    ? mapMeal(meal)
                    : mapMed(med);
        }

        return nextMeal
                .map(this::mapMeal)
                .orElseGet(()->mapMed(nextMed.get()));
    }

    //after 'done' status update, return next schedule
    @Transactional
    public UpcomingScheduleResponse confirmAndGetNext(String loginId){
        UpcomingScheduleResponse current=getNext(loginId);
        //check status as true
        if("MEAL".equals(current.type())){
            mealScheduleService.updateMealScheduleStatus(current.scheduleId(), true);
        }
        else{
            medicationScheduleService.updateIsTaken(loginId, current.scheduleId(),
                    new UpdateMedicationStatusRequest(true,null,null));
        }
        //return next schedule
        return getNext(loginId);
    }

    //after 'deny' status update, return next schedule
    //if currentIgnoreCnt == ignoreCnt => get FCM push
    @Transactional
    public UpcomingScheduleResponse denyAndGetNext(String loginId){

        UpcomingScheduleResponse current=getNext(loginId);
        User user=userService.getUserByLoginIdOrThrow(loginId);

        //status update
        if("MEAL".equals(current.type())){
            mealScheduleService.updateMealScheduleStatus(current.scheduleId(),false);
        }
        else{
            medicationScheduleService.updateIsTaken(loginId, current.scheduleId(),
                    new UpdateMedicationStatusRequest(false,null,null));
        }

        //CurrentIgnoreCount +1
        Long currentIgnore=userService.addUserCurrentIgnoreCount(loginId);
        Long maxIgnore=user.getIgnoreCnt().longValue();

        //if currentIgnore==maxIgnore send FCM push
        if(currentIgnore.equals(maxIgnore)){
            try{
                String token=subscriptionService.getTokenForUser(user.getId());
                pushNotificationService.sendNotification(
                        token,
                        "긴급 상황 알림 발생",
                        "현재 알람 거절 횟수("+currentIgnore+"회)가 최대 허용 횟수에 도달했습니다."
                );
            }catch (FirebaseMessagingException e){
                //TODO: 푸시 알람 실패 처리
            }
        }

        return getNext(loginId);

    }

    private UpcomingScheduleResponse mapMeal(MealSchedule mealSchedule){
        return new UpcomingScheduleResponse(mealSchedule);
    }

    private UpcomingScheduleResponse mapMed(MedicationSchedule medicationSchedule){
        return new UpcomingScheduleResponse(medicationSchedule);
    }
}
