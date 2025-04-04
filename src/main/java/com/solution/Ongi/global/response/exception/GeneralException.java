package com.solution.Ongi.global.response.exception;

import com.solution.Ongi.global.response.code.BaseCode;

public class GeneralException extends RuntimeException {
    private final BaseCode code;

    public GeneralException(BaseCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public BaseCode getCode() {
        return code;
    }

}
