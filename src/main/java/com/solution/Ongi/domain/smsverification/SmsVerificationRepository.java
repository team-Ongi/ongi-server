package com.solution.Ongi.domain.smsverification;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsVerificationRepository extends JpaRepository<SmsVerification, Long> {
    Optional<SmsVerification> findTopByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
    void deleteByPhoneNumber(String phoneNumber);
}
