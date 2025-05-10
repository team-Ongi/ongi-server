package com.solution.Ongi.infra.firebase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    Optional<Subscription> findByUserId(Long userId);
    List<Subscription> findAllByUserId(Long userId);
    Optional<Subscription> findByRegistrationToken(String registrationToken);
    void deleteByRegistrationToken(String token);
}
