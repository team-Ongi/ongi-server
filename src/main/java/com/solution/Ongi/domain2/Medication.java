package com.solution.Ongi.domain2;

import com.solution.Ongi.domain.user.User;
import jakarta.persistence.*;

import java.time.format.DateTimeFormatter;

public class Medication {

    @Id
    @GeneratedValue
    @Column(name = "medication_id")
    private Long id;

    private String medication_title;
    //format type ofLocalizedTime
    private DateTimeFormatter medication_time;

    @ManyToOne(cascade= CascadeType.ALL,fetch = FetchType.LAZY)//지연 로딩
    @JoinColumn(name = "member_id")
    private User user;
}
