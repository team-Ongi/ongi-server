package com.solution.Ongi.domain.smsverification;

import com.solution.Ongi.global.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sms_verification")
public class SmsVerification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String code;

    @Default
    private Boolean isVerified = false;

    public void verify() {
        this.isVerified = true;
    }

}
