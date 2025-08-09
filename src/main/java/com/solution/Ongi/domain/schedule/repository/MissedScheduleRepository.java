package com.solution.Ongi.domain.schedule.repository;

import com.solution.Ongi.domain.schedule.MissedSchedule;
import com.solution.Ongi.domain.schedule.enums.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissedScheduleRepository extends JpaRepository<MissedSchedule, Long> {
    boolean existsByScheduleTypeAndScheduledId(ScheduleType type,Long scheduleId);
}
