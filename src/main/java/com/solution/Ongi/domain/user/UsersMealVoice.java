package com.solution.Ongi.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users_meal_voice")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UsersMealVoice {
    @Id
    private Long userId;

    @Column(name = "voice_file_url")
    private String voiceFileUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void updateVoiceFileUrl(String voiceFileUrl) {
        this.voiceFileUrl = voiceFileUrl;
    }
}
