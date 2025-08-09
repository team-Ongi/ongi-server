package com.solution.Ongi.domain.schedule;

import com.solution.Ongi.domain.schedule.enums.ScheduleType;
import com.solution.Ongi.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissedSchedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name="schedule_type")
    private ScheduleType scheduleType;

    @Column(name="scheduled_date")
    private LocalDate scheduledDate;

    @Column(name="scheduled_time")
    private LocalTime scheduledTime;

}
