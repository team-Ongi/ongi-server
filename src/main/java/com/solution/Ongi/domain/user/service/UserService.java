package com.solution.Ongi.domain.user.service;

import com.solution.Ongi.domain.user.dto.SignupRequest;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.exception.UserNotFoundException;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User signup(SignupRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new GeneralException(ErrorStatus.ERROR_STATUS);
        }

        User user = User.builder()
            .loginId(request.loginId())
            .password(request.password())
            .guardianName(request.guardianName())
            .guardianPhone(request.guardianPhone())
            .seniorName(request.seniorName())
            .seniorAge(request.seniorAge())
            .seniorPhone(request.seniorPhone())
            .relation(request.relation())
            .alertMax(request.alertMax())
            .build();

        return userRepository.save(user);

    }

    //user 검색
    public User getUserByIdOrThrow(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(userId));
    }


}
