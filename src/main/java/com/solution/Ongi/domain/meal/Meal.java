package com.solution.Ongi.domain.meal;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.global.base.BaseTimeEntity;
import com.solution.Ongi.domain.meal.enums.MealType;
import jakarta.persistence.*;
import java.time.LocalTime;
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

    //mealSchedule 일대다 매핑
    @OneToOne(mappedBy = "meal",cascade = CascadeType.ALL)
    private MealSchedule mealSchedule;
    //일정-달력 삭제 미반영 -> CASCADE 막기: persist 로 영속성 부여 (삭제 반영 안 되는지 확인 필요)
}
