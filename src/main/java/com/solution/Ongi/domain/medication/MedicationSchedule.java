package com.solution.Ongi.domain.medication;

import com.solution.Ongi.global.base.BaseTimeEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "medication_schedule")
public class MedicationSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    private Medication medication;

    @Column(name = "status")
    private boolean status;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Column(name = "not_taken_reason")
    private String notTakenReason;

    @Column(name= "remind_later")
    private Boolean remindLater;

    public void reset() {
        this.status = false;
        this.scheduledDate = LocalDate.now();
    }

    public void markAsTaken() {
        this.status = true;
    }

    public void markAsNotTaken(String notTakenReason) {
        this.status = false;
        this.notTakenReason = notTakenReason;
    }

    public void updateScheduleTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

}
