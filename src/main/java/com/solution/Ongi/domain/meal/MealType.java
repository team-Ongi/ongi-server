package com.solution.Ongi.domain.meal;

import com.solution.Ongi.domain.medication.Medication;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "meal_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MealType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_type_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Column(name = "meal_type")
    private Integer mealType; // 0~2 값 허용, Enum으로 변환 원하면 별도 처리 가능
}