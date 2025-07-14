package com.solution.Ongi.domain.user.repository;

import com.solution.Ongi.domain.user.UsersMedicationVoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersMedicationVoiceRepository extends JpaRepository<UsersMedicationVoice, Long> {
    @Query("""
    SELECT ums.voiceFileUrl
    FROM UsersMedicationVoice ums
    WHERE ums.userId = :userId
    """)
    List<String> findVoiceFileUrlByUserId(@Param("userId") Long userId);

    List<UsersMedicationVoice> findAllByUserId(Long userId);
}
