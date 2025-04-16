package com.solution.Ongi.global.response.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    String getCode();
    HttpStatus getHttpStatus();
    String getMessage();
}
