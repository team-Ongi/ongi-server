package com.solution.Ongi.global.response.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    int getCode();
    HttpStatus getHttpStatus();
    String getMessage();
}
