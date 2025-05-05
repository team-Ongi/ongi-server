package com.solution.Ongi.domain.medication;

import com.solution.Ongi.global.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class MedicationSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    private Medication medication;

    private boolean isTaken;

    private LocalDate checkDate;

    private LocalTime medicationTime;

    private String reason;

    private Integer remindAfterMinutes;

    public void reset() {
        this.isTaken = false;
        this.checkDate = LocalDate.now();
    }

    public void markAsTaken() {
        this.isTaken = true;
    }

    public void markAsNotTaken(String reason, Integer remindAfterMinutes) {
        this.isTaken = false;
        this.reason = reason;
        this.remindAfterMinutes = remindAfterMinutes;
    }

}
