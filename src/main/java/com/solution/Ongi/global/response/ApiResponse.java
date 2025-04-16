package com.solution.Ongi.global.response;

import com.solution.Ongi.global.response.code.BaseCode;
import com.solution.Ongi.global.response.code.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, SuccessStatus.SUCCESS.getCode(), SuccessStatus.SUCCESS.getMessage(), data);
    }

    public static ApiResponse<?> error (BaseCode code) {
        return new ApiResponse<>(false, code.getCode(), code.getMessage(), null);
    }

}
