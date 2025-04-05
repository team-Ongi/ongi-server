package com.solution.Ongi.domain2;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.enums.MealType;
import jakarta.persistence.*;

import java.time.format.DateTimeFormatter;

public class Meal {

    @Id @GeneratedValue
    @Column(name = "meal_id")
    private Long id;

    private MealType meal_type;
    //format type ofLocalizedTime
    private DateTimeFormatter meal_time;

    @ManyToOne(cascade=CascadeType.ALL,fetch = FetchType.LAZY)//지연 로딩
    @JoinColumn(name = "user_id")
    private User user;

    //일정-달력 삭제 미반영 -> CASCADE 막기
}
