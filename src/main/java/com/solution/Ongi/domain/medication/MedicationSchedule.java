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

    @Column(name = "is_taken")
    private boolean isTaken;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Column(name = "not_taken_reason")
    private String notTakenReason;

    public void reset() {
        this.isTaken = false;
        this.scheduledDate = LocalDate.now();
    }

    public void markAsTaken() {
        this.isTaken = true;
    }

    public void markAsNotTaken(String notTakenReason, Integer remindAfterMinutes) {
        this.isTaken = false;
        this.notTakenReason = notTakenReason;
    }

}
