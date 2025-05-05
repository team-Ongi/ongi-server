package com.solution.Ongi.domain.meal;

import com.solution.Ongi.global.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;


/*
복사 생성 로직
* meal 정보를(user id, create dto) 기반으로 mealSchedule 매일 자정 생성 -> 생성 구분 : meal_type
    **근데 이렇게 여러개로 관리해버리면 객체 너무 많아짐 -> 부하 오질텐데
    **=> 하나로 유지하되(매시간 null로 초기화) 달력 기능은 누락한 날짜 보관 엔티티 분리하는거 어떱니까 => ok~
    **OneToMany 긴한데 개수 유지. 엄연히 말하면 meal_type에 매핑된것...e
* meal 은 건들지 x (const 로 접근)
 */

/*
meal schedule list 를 멤버가 관리? Meal이 관리?
 */

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealSchedule extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "meal_schedule_id")
    private Long id;

    private LocalTime meal_schedule_time;

    @Setter
    private boolean status;    //default=false

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="meal_id")
    private Meal meal;

}
