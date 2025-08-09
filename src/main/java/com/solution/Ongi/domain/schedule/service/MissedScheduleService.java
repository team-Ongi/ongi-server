package com.solution.Ongi.domain.schedule.service;

import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.MealSchedule;
import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.schedule.MissedSchedule;
import com.solution.Ongi.domain.schedule.dto.MissedCandidateDto;
import com.solution.Ongi.domain.schedule.enums.ScheduleType;
import com.solution.Ongi.domain.schedule.repository.MissedScheduleRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MissedScheduleService {

    private final UserService userService;
    private final MealScheduleRepository mealScheduleRepository;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MissedScheduleRepository missedScheduleRepository;

    @Transactional
    public void detectAndRecordMostRecentMissed(String loginId){
        User user=userService.getUserByLoginIdOrThrow(loginId);
        LocalDate today= LocalDate.now();
        LocalTime now=LocalTime.now();

        Optional<MealSchedule> optPrevMeal=mealScheduleRepository
                .findFirstByMeal_User_IdAndScheduledDateAndScheduledTimeLessThanEqualOrderByScheduledTimeDesc(
                        user.getId(), today, now
                );
        Optional<MedicationSchedule> optPrevMed =medicationScheduleRepository
                .findFirstByMedication_User_IdAndScheduledDateAndScheduledTimeLessThanEqualOrderByScheduledTimeDesc(
                        user.getId(), today, now
                );

        // 둘 다 없으면 기록x
        if (optPrevMeal.isEmpty()&& optPrevMed.isEmpty()) return;

        // MEAL, MED 중 더 늦은 (최근) 시간 하나 선택
        // 동시간 존재 시 MEAL 우선
        if (optPrevMeal.isPresent() && optPrevMed.isPresent()) {
            MealSchedule meal = optPrevMeal.get();
            MedicationSchedule med = optPrevMed.get();

            boolean chooseMeal = !meal.getScheduledTime().isBefore(med.getScheduledTime()); // 동시각이면 MEAL 우선
            if (chooseMeal) {
                if (!meal.getStatus()) {
                    recordIfMissedSchedule(user, MissedCandidateDto.fromMeal(meal));
                    userService.addUserCurrentIgnoreCount(user.getLoginId());
                }
            } else {
                if (!med.isStatus()) {
                    recordIfMissedSchedule(user, MissedCandidateDto.fromMedication(med));
                    userService.addUserCurrentIgnoreCount(user.getLoginId());
                }
            }
            return;
        }

            // 하나만 존재하는 경우
            // missedSchedule 에 기록하고 currentIgnoreCnt+1
        if (optPrevMeal.isPresent()){
            if(!optPrevMeal.get().getStatus()){
                recordIfMissedSchedule(user,MissedCandidateDto.fromMeal(optPrevMeal.get()));
                userService.addUserCurrentIgnoreCount(user.getLoginId());
            }
            return;
        }

        if(!optPrevMed.get().isStatus()){
            recordIfMissedSchedule(user, MissedCandidateDto.fromMedication(optPrevMed.get()));
            userService.addUserCurrentIgnoreCount(user.getLoginId());
        }

    }

    // 선택된 하나의 meal/med 의 status가 false일 때 missedSchedule에 기록
    private void recordIfMissedSchedule(User user, MissedCandidateDto c) {

        if (c.status()) return;

        // 이미 존재 시 스킵 (중복 방지)
        if (missedScheduleRepository.existsByScheduleTypeAndScheduledId(c.type(), c.scheduleId()))
            return;

        MissedSchedule missed = MissedSchedule.builder()
                .user(user)
                .scheduleType(c.type())
                .scheduledDate(c.scheduledDate())
                .scheduledTime(c.scheduledTime())
                .build();

        missedScheduleRepository.save(missed);
    }
}
