package com.solution.Ongi.domain.schedule.service;

import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.meal.service.MealScheduleService;
import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationStatusRequest;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.medication.service.MedicationScheduleService;
import com.solution.Ongi.domain.schedule.dto.UpcomingScheduleResponse;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
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

    @Transactional(readOnly = true)
    public UpcomingScheduleResponse getNext(String loginId){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        LocalDate today=LocalDate.now();
        LocalTime currentTime=LocalTime.now();

        Optional<MealSchedule> nextMeal=mealScheduleRepository
                .findFirstByMeal_User_IdAndMealScheduleDateAndStatusFalseAndMealScheduleTimeAfterOrderByMealScheduleTimeAsc(
                        user.getId(),today,currentTime
                );
        Optional<MedicationSchedule> nextMed=medicationScheduleRepository
                .findFirstByMedication_User_IdAndCheckDateAndIsTakenFalseAndMedicationTimeAfterOrderByMedicationTimeAsc(
                        user.getId(),today,currentTime
                );

        if(nextMeal.isEmpty()&&nextMed.isEmpty()){
            throw new RuntimeException("남은 스케줄이 없습니다.");
        }

        //if both exist, return closer schedule
        if(nextMeal.isPresent()&&nextMed.isPresent()){
            MealSchedule meal=nextMeal.get();
            MedicationSchedule med=nextMed.get();
            if(meal.getMealScheduleTime().isBefore(med.getMedicationTime())){
                return mapMeal(meal);
            }else return mapMed(med);
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

    @Transactional
    public UpcomingScheduleResponse denyAndGetNext(String loginId){
        UpcomingScheduleResponse current=getNext(loginId);
        if("MEAL".equals(current.type())){
            mealScheduleService.updateMealScheduleStatus(current.scheduleId(),false);
        }
        else{
            medicationScheduleService.updateIsTaken(loginId, current.scheduleId(),
                    new UpdateMedicationStatusRequest(false,null,null));
        }
        userService.addCurrentIgnoreCount(loginId);
        return getNext(loginId);
    }


    private UpcomingScheduleResponse mapMeal(MealSchedule mealSchedule){
        return new UpcomingScheduleResponse(mealSchedule);
    }

    private UpcomingScheduleResponse mapMed(MedicationSchedule medicationSchedule){
        return new UpcomingScheduleResponse(medicationSchedule);
    }
}
