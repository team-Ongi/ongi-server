package com.solution.Ongi.domain.meal;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.global.base.BaseTimeEntity;
import com.solution.Ongi.domain.meal.enums.MealType;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
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
public class Meal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MealType meal_type;
    private LocalTime meal_time;

    //user 다대일 매핑
    @ManyToOne(fetch = FetchType.LAZY)//지연 로딩
    @JoinColumn(name = "user_id")
    private User user;

    //mealSchedule ver2
    @Builder.Default
    @OneToMany(mappedBy = "meal",cascade = CascadeType.ALL)
    private List<MealSchedule> mealSchedules=new ArrayList<>();
}
