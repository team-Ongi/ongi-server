package com.solution.Ongi.domain.push.repository;

import com.solution.Ongi.domain.push.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByToken(String token);
    List<DeviceToken> findAllByUser_IdAndActiveTrue(Long userId);
}
