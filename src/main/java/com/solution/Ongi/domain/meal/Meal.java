package com.solution.Ongi.domain.meal;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.enums.MealType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Meal {

    @Id @GeneratedValue
    @Column(name = "meal_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MealType meal_type;

    private LocalTime meal_time;

    @ManyToOne(fetch = FetchType.LAZY)//지연 로딩
    @JoinColumn(name = "user_id")
    private User user;

    //일정-달력 삭제 미반영 -> CASCADE 막기
}
