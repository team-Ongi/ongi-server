package com.solution.Ongi.domain.schedule.service;

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

            boolean mealIsLaterOrEqual = !meal.getScheduledTime().isBefore(med.getScheduledTime()); // 동시각이면 MEAL 우선

            MissedCandidateDto candidate =
                    mealIsLaterOrEqual ? MissedCandidateDto.fromMeal(meal)
                            : MissedCandidateDto.fromMedication(med);

            recordIfMissedSchedule(user, candidate);

        }

    }

    private void recordIfMissedSchedule(User user, MissedCandidateDto c) {
        // 선택된 하나의 meal/med 의 status가 false일 때만 기록

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
