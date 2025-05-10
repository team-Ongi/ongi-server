package com.solution.Ongi.infra.firebase;

import com.solution.Ongi.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name="subscription")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_token",nullable = false,unique = true)
    private String registrationToken;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id")
    private User user;

}
