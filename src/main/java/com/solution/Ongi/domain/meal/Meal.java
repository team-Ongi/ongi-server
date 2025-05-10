package com.solution.Ongi.domain.meal;

import com.solution.Ongi.domain.user.User;
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

public class Meal{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MealType mealType;
    private LocalTime mealTime;

    //user 다대일 매핑
    @ManyToOne(fetch = FetchType.LAZY)//지연 로딩
    @JoinColumn(name = "user_id")
    private User user;

    //mealSchedule 일대다 매핑
    @Builder.Default
    @OneToMany(mappedBy = "meal",cascade = CascadeType.ALL)
    private List<MealSchedule> mealSchedules=new ArrayList<>();
}
