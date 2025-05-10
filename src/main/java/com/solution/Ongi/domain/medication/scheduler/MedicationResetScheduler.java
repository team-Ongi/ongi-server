package com.solution.Ongi.domain.medication.scheduler;

import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.enums.IntakeTiming;
import com.solution.Ongi.domain.medication.enums.MedicationType;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MedicationResetScheduler {
    private final MedicationScheduleRepository scheduleRepository;
    private final MedicationRepository medicationRepository;
    private final MealRepository mealRepository;

    // 매일 자정 스케줄러 실행
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void resetMedicationSchedules() {
        List<Medication> medications = medicationRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Medication medication : medications) {
            if (medication.getType().equals(MedicationType.FIXED_TIME)) {
                for (LocalTime time : medication.getMedicationTimes()) {
                    MedicationSchedule schedule = MedicationSchedule.builder()
                        .medication(medication)
                        .checkDate(today)
                        .medicationTime(time)
                        .isTaken(false)
                        .build();
                    scheduleRepository.save(schedule);
                }
            } else if (medication.getType().equals(MedicationType.MEAL_BASED)) {
                Long userId = medication.getUser().getId();
                List<Meal> meals = mealRepository.findByUserId(userId);

                for (MealType mealType : medication.getMealTypes()) {
                    Meal meal = meals.stream()
                        .filter(m -> m.getMealType() == mealType)
                        .findFirst()
                        .orElse(null);

                    if (meal == null)
                        continue;

                    int offset = medication.getIntakeTiming() == IntakeTiming.AFTER_MEAL
                        ? medication.getRemindAfterMinutes()
                        : -medication.getRemindAfterMinutes();

                    LocalTime scheduleTime = meal.getMealTime().plusMinutes(offset);

                    MedicationSchedule schedule = MedicationSchedule.builder()
                        .medication(medication)
                        .checkDate(today)
                        .medicationTime(scheduleTime)
                        .isTaken(false)
                        .build();

                    scheduleRepository.save(schedule);
                }
            }
        }
    }

}
