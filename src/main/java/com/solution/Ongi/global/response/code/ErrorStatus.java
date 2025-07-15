package com.solution.Ongi.global.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 예시
    ERROR_STATUS(400, HttpStatus.BAD_REQUEST, "Bad Request"),

    // User
    USER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "해당 Id는 존재하지 않습니다."),
    ALREADY_EXIST_USER(409, HttpStatus.CONFLICT, "이미 존재하는 UserId입니다."),
    INVALID_PASSWORD(401, HttpStatus.UNAUTHORIZED, "비밀번호가 유효하지 않습니다"),

    // Auth
    UNAUTHORIZED_ACCESS(403, HttpStatus.FORBIDDEN, "해당 리소스에 접근할 권한이 없습니다."),
    INVALID_TOKEN(401, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_MISMATCH(401, HttpStatus.UNAUTHORIZED, "일치하지 않는 토큰입니다"),
    LOGIN_ID_NOT_FOUND(404, HttpStatus.NOT_FOUND, "해당 ID가 존재하지 않습니다."),
    SAME_AS_OLD_PASSWORD(409, HttpStatus.CONFLICT, "새 비밀번호는 이전과 같을 수 없습니다."),

    // Sms
    VERIFICATION_CODE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "해당 번호로 인증 요청이 없습니다."),
    VERIFICATION_NOT_COMPLETED(400, HttpStatus.BAD_REQUEST, "인증이 완료되지 않았습니다."),
    VERIFICATION_EXPIRED(400, HttpStatus.BAD_REQUEST, "인증번호 유효 시간이 만료되었습니다."),
    VERIFICATION_CODE_MISMATCH(400, HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),

    // Medication
    MEDICATION_NOT_FOUND(404, HttpStatus.NOT_FOUND, "약 정보가 존재하지 않습니다."),

    // Medication Schedule
    MEDICATION_SCHEDULE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "해당 복약 스케줄이 존재하지 않습니다."),

    // Meal Schedule
    MEAL_SCHEDULE_NOT_REGISTER(409, HttpStatus.CONFLICT, "식사 시간이 등록되어 있지 않아 약을 등록할 수 없습니다."),

    // 서버 에러
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버에러");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}