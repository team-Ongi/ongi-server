package com.solution.Ongi.global.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode{
    SUCCESS_200(200, HttpStatus.OK, "성공입니다."),
    SUCCESS_201(201, HttpStatus.CREATED, "성공입니다."),
    SUCCESS_204(204, HttpStatus.NO_CONTENT, "성공입니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
