package com.solution.Ongi.domain.meal;

import com.solution.Ongi.global.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "meal_schedule_id")
    private Long id;

    private LocalTime mealScheduleTime;
    private LocalDate mealScheduleDate;

    @Setter
    private boolean status;    //default=false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

}