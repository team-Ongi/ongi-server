package com.solution.Ongi.global.response.exception;

import com.solution.Ongi.global.response.ApiResponse;
import com.solution.Ongi.global.response.code.BaseCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(GeneralException ex, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/v3/api-docs")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        BaseCode code = ex.getCode();

        return ResponseEntity
            .status(code.getHttpStatus())
            .body(ApiResponse.error(code));
    }

}
