package com.solution.Ongi.domain.push.service;

import com.solution.Ongi.domain.push.DeviceToken;
import com.solution.Ongi.domain.push.repository.DeviceTokenRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserService userService;

    @Transactional
    public void register(String loginId, String token, String platform, String deviceId){
        User user=userService.getUserByLoginIdOrThrow(loginId);

        DeviceToken dt=deviceTokenRepository.findByToken(token).orElse(null);
        if(dt==null){
            deviceTokenRepository.save(DeviceToken.createDeviceToken(user,token,platform,deviceId));
            return;
        }

        // TODO: device 개수 제한
        // final int LIMIT + 오래된 토큰 deactivate

        dt.rebindTo(user,platform,deviceId);
        dt.markActive();
        dt.touchLastSeen();
    }

    @Transactional
    public void deactivateByToken(String token){
        deviceTokenRepository.findByToken(token)
                .ifPresent(DeviceToken::markInactive);
    }

    @Transactional(readOnly = true)
    public List<String> getActiveTokenByUserId(Long userId){
        return deviceTokenRepository.findAllByUser_IdAndActiveTrue(userId)
                .stream()
                .map(DeviceToken::getToken)
                .toList();
    }

    @Transactional
    public void deactivateAllByToken(Collection<String> tokens){
        if(tokens==null || tokens.isEmpty()) return;
        tokens.forEach(this::deactivateByToken);
    }
}
