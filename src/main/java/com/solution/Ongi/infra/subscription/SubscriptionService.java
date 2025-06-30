package com.solution.Ongi.infra.subscription;

import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    //store new token
    @Transactional
    public void save(Long userId,String registrationToken){
        User user=userService.getUserByIdOrThrow(userId);

        //덮어쓰기 방지
        subscriptionRepository.findByRegistrationToken(registrationToken)
                .ifPresent(subscription -> subscriptionRepository.delete(subscription));

        Subscription subscription= Subscription.builder()
                .user(user)
                .registrationToken(registrationToken)
                .build();
        subscriptionRepository.save(subscription);
    }

    //필요한 토큰 가져옴
    @Transactional(readOnly = true)
    public String getTokenForUser(Long userId){
        return subscriptionRepository.findByUserId(userId)
                .map(Subscription::getRegistrationToken)
                .orElseThrow(()->new RuntimeException("구독 토큰이 없습니다."));
    }
}
