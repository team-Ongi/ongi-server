package com.solution.Ongi.domain.agreement;

import com.solution.Ongi.global.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Agreement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Boolean pushAgreement = false;

    @Builder.Default
    private Boolean voiceAgreement = false;

    @Builder.Default
    private Boolean backgroundAgreement = false;

    @Builder.Default
    private Boolean personalInfoAgreement = false;

    public void markPersonalInfoAgreement() {
        this.personalInfoAgreement = true;
    }



}
