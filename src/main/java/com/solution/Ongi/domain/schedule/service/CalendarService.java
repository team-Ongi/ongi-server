package com.solution.Ongi.domain.schedule.service;

import com.solution.Ongi.domain.meal.repository.MealScheduleRepository;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.schedule.MissedSchedule;
import com.solution.Ongi.domain.schedule.dto.CalendarDayStatusResponse;
import com.solution.Ongi.domain.schedule.enums.ScheduleType;
import com.solution.Ongi.domain.schedule.repository.MissedScheduleRepository;
import com.solution.Ongi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final MissedScheduleRepository missedScheduleRepository;
    private final UserService userService;

    public List<CalendarDayStatusResponse> getMissedStatus(
            String loginId, LocalDate start, LocalDate end) {

        Long userId = userService.getUserByLoginIdOrThrow(loginId).getId();

        // 한 달치 Missed 전부 로딩
        List<MissedSchedule> missedList = missedScheduleRepository
                .findAllByUser_IdAndScheduledDateBetweenOrderByScheduledDateAscScheduledTimeAsc(
                        userId, start, end
                );

        // 날짜별 플래그 집계 (하루에 여러 건 있어도 true 한 번만)
        Set<LocalDate> mealDays = new HashSet<>();
        Set<LocalDate> medDays  = new HashSet<>();

        for (MissedSchedule m : missedList) {
            LocalDate date = m.getScheduledDate();
            if (m.getScheduleType() == ScheduleType.MEAL) {
                mealDays.add(date);
            } else if (m.getScheduleType() == ScheduleType.MEDICATION) {
                medDays.add(date);
            }
        }

        // 누락이 있었던 날짜만 오름차순으로 반환
        Set<LocalDate> allDates = new TreeSet<>();
        allDates.addAll(mealDays);
        allDates.addAll(medDays);

        List<CalendarDayStatusResponse> result = new ArrayList<>(allDates.size());
        for (LocalDate date : allDates) {
            result.add(new CalendarDayStatusResponse(
                    date,
                    mealDays.contains(date),
                    medDays.contains(date)
            ));
        }
        return result;
    }
}