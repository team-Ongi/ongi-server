package com.solution.Ongi.domain.user;

import com.solution.Ongi.domain.agreement.Agreement;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.user.enums.AlertInterval;
import com.solution.Ongi.domain.user.enums.RelationType;
import com.solution.Ongi.global.base.BaseTimeEntity;
import com.solution.Ongi.infra.subscription.Subscription;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name="users")
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    private String loginId;

    private String password;

    private String guardianName;

    @Column(name = "guardian_phone")
    private String guardianPhone;

    private String seniorName;

    private Integer seniorAge;

    @Column(name = "senior_phone")
    private String seniorPhone;

    private Integer ignoreCnt;

    @Schema(
            description = "유저 생성시와 스케줄 서비스에서만 생성 및 업데이트합니다.",
            accessMode  = Schema.AccessMode.READ_ONLY
    )
    private Integer currentIgnoreCnt;

    @Enumerated(EnumType.STRING)
    private RelationType relation;

    @Enumerated(EnumType.STRING)
    private AlertInterval alertMax;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true ,fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id")
    private Agreement agreement;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    @JoinColumn(name="senior_agreement_id")
    private Agreement seniorAgreement;

    private String refreshToken;

    @Column(name = "voice_file_key")
    private String voiceFileKey;

    @Column(name = "is_service_agreed")
    private Boolean isServiceAgreed;

    @Column(name= "is_senior_service_agreed")
    private Boolean isSeniorServiceAgreed;

    @Builder.Default
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Meal> meals=new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Medication> medications=new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy="user",cascade = CascadeType.ALL)
    private List<Subscription> subscriptions=new ArrayList<>();

    public void addCurrentIgnoreCnt(){
        this.currentIgnoreCnt++;
    }

    public void updateIgnoreCnt(Integer ignoreCnt){ this.ignoreCnt=ignoreCnt;}


    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateVoiceFileKey(String voiceFileKey) {
        this.voiceFileKey = voiceFileKey;
    }

    public void updateIsServiceAgreed(Boolean isServiceAgreed) {
        this.isServiceAgreed = isServiceAgreed;
    }

    public void updateIsSeniorServiceAgreed(Boolean isSeniorServiceAgreed){this.isSeniorServiceAgreed=isSeniorServiceAgreed;}

}
