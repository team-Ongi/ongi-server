package com.solution.Ongi.domain.medication;

import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.medication.enums.IntakeTiming;
import com.solution.Ongi.domain.medication.enums.MedicationType;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.global.base.BaseTimeEntity;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "medication")
public class Medication extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    private Long id;

    @Column(name = "medication_name")
    private String medicationName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MedicationType medicationType;

    // FIXED_TIME 용
    @ElementCollection
    @CollectionTable(name = "medication_time", joinColumns = @JoinColumn(name = "medication_id"))
    @Column(name = "time")
    private List<LocalTime> medicationTimes;

    // MEAL_BASED 용
    @Enumerated(EnumType.STRING)
    private IntakeTiming intakeTiming;

    @ElementCollection
    @CollectionTable(name = "meal_type", joinColumns = @JoinColumn(name = "medication_id"))
    @Column(name = "meal_type")
    private List<MealType> mealTypes; // BREAKFAST, LUNCH, DINNER

    private Integer remindAfterMinutes;

    @ManyToOne(fetch = FetchType.LAZY)//지연 로딩
    @JoinColumn(name = "user_id")
    private User user;

    public void updateFixedTime(String name, List<LocalTime> timeList) {
        this.medicationName = name;
        this.medicationTimes = timeList;
        this.medicationType = MedicationType.FIXED_TIME;
    }

    public void updateMealBased(String name, IntakeTiming timing, List<MealType> mealTypes, Integer remindAfterMinutes) {
        this.medicationName = name;
        this.intakeTiming = timing;
        this.mealTypes = mealTypes;
        this.remindAfterMinutes = remindAfterMinutes;
        this.medicationType = MedicationType.MEAL_BASED;
    }
}
