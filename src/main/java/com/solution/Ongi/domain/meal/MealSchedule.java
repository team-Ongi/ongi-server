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
@Table(name = "meal_schedule")
public class MealSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "meal_schedule_id")
    private Long id;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Column(name = "not_taken_reason")
    private String notTakenReason;

    @Column(name= "remind_later")
    private Boolean remindLater;

    @Setter
    @Column(name = "status")
    private boolean status;    //default=false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    public void markAsTaken(){ this.status=true; }

    public void markAsNotTaken(String notTakenReason){
        this.status=false;
        this.notTakenReason=notTakenReason;
    }

    public boolean getStatus(){
        return this.status;
    }

}