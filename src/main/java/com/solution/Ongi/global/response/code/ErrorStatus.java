package com.solution.Ongi.global.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 예시, 이후 필요한 에러코드 추가
    ERROR_STATUS(400, HttpStatus.BAD_REQUEST, "Bad Request");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

}
