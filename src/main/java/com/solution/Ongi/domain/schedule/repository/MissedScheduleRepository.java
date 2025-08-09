package com.solution.Ongi.domain.schedule.repository;

import com.solution.Ongi.domain.schedule.MissedSchedule;
import com.solution.Ongi.domain.schedule.enums.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MissedScheduleRepository extends JpaRepository<MissedSchedule, Long> {
    boolean existsByScheduleTypeAndScheduledId(ScheduleType type,Long scheduleId);

    List<MissedSchedule>
    findAllByUser_IdAndScheduledDateBetweenOrderByScheduledDateAscScheduledTimeAsc(
            Long userId, LocalDate start, LocalDate end
    );
}
