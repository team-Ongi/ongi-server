package com.solution.Ongi.domain.medication;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.global.base.BaseTimeEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
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
public class Medication extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    private Long id;

    private String medication_title;

    @ElementCollection
    @CollectionTable(name = "medication_time", joinColumns = @JoinColumn(name = "medication_id"))
    @Column(name = "time")
    private List<LocalTime> medication_time;

    @ManyToOne(fetch = FetchType.LAZY)//지연 로딩
    @JoinColumn(name = "user_id")
    private User user;

    public void update(String title, List<LocalTime> timeList) {
        this.medication_title = title;
        this.medication_time = timeList;
    }
}
