package com.solution.Ongi.global.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 예시, 이후 필요한 에러코드 추가
    ERROR_STATUS(400, HttpStatus.BAD_REQUEST, "Bad Request"),

    // User
    USER_NOT_FOUND(400, HttpStatus.BAD_REQUEST, "존재 하지 않는 UserId입니다."),
    INVALID_PASSWORD(400, HttpStatus.BAD_REQUEST, "비밀번호가 유효하지 않습니다"),


    ;

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

}
