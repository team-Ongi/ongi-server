package com.solution.Ongi.domain.user;

import com.solution.Ongi.domain.user.enums.AlertInterval;
import com.solution.Ongi.domain.user.enums.RelationType;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.medication.Medication;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name="users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    private String loginId;

    private String password;

    private String guardianName;

    private String guardianPhone;

    private String seniorName;

    private Integer seniorAge;

    private String seniorPhone;

    @Enumerated(EnumType.STRING)
    private RelationType relation;

    @Enumerated(EnumType.STRING)
    private AlertInterval alertMax;

    @Builder.Default
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Meal> meals=new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Medication> medications=new ArrayList<>();

}
