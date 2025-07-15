package com.solution.Ongi.global.response;

import com.solution.Ongi.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private Integer code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data, BaseCode baseCode) {
        return new ApiResponse<>(true, baseCode.getCode(),baseCode.getMessage(), data);
    }

    public static ApiResponse<?> error (BaseCode code) {
        return new ApiResponse<>(false, code.getCode(), code.getMessage(), null);
    }

}
