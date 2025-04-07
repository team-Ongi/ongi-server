package com.solution.Ongi.domain.medication;

import com.solution.Ongi.domain.user.User;
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
public class Medication {

    @Id
    @GeneratedValue
    @Column(name = "medication_id")
    private Long id;

    private String medication_title;
    private LocalTime medication_time;

    @ManyToOne(fetch = FetchType.LAZY)//지연 로딩
    @JoinColumn(name = "user_id")
    private User user;
}
