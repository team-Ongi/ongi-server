package com.solution.Ongi.domain.schedule.service;

import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.schedule.dto.CalendarDayStatusResponse;
import com.solution.Ongi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MealScheduleRepository mealScheduleRepository;
    private final UserService userService;

    public List<CalendarDayStatusResponse> getMissedStatus(
            String loginId, LocalDate start, LocalDate end){

        Long userId=userService.getUserByLoginIdOrThrow(loginId).getId();

        List<LocalDate> mealDates=
                mealScheduleRepository
                        .findDistinctMealScheduleDateByMeal_User_IdAndScheduledDateBetweenAndStatusFalse(userId,start,end);

        //TODO: medication 추가
        List<LocalDate> medicationDates=
                medicationScheduleRepository
                        .findMissedDatesByUserAndDateRange(userId,start,end);

        Set<LocalDate> allDates=new TreeSet<>();
        allDates.addAll(mealDates);
        allDates.addAll(medicationDates);

        return allDates.stream()
                .map(date->new CalendarDayStatusResponse(
                        date,
                        mealDates.contains(date),
                        medicationDates.contains(date)
                ))
                .toList();
    }
}