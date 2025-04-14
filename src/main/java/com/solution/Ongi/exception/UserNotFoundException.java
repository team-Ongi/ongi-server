package com.solution.Ongi.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long userId) {
        super("ID가 " + userId + "인 사용자를 찾을 수 없습니다.");
    }
}
