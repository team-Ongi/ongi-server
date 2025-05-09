package com.solution.Ongi.global.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 예시, 이후 필요한 에러코드 추가
    ERROR_STATUS("COMMON400", HttpStatus.BAD_REQUEST, "Bad Request"),

    // User
    USER_NOT_FOUND("USER400", HttpStatus.BAD_REQUEST, "해당 UserId는 존재하지 않습니다."),
    ALREADY_EXIST_USER("USER401", HttpStatus.BAD_REQUEST, "이미 존재하는 UserId입니다."),
    INVALID_PASSWORD("USER402", HttpStatus.BAD_REQUEST, "비밀번호가 유효하지 않습니다"),

    // Auth
    UNAUTHORIZED_ACCESS("AUTH400", HttpStatus.FORBIDDEN, "해당 리소스에 접근할 권한이 없습니다."),
    INVALID_TOKEN("AUTH401", HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    TOKEN_MISMATCH("AUTH402", HttpStatus.BAD_REQUEST, "일치하지 않는 토큰입니다"),



    // Sms
    VERIFICATION_CODE_NOT_FOUND("SMS400", HttpStatus.BAD_REQUEST, "해당 번호로 인증 요청이 없습니다."),
    VERIFICATION_NOT_COMPLETED("SMS401", HttpStatus.BAD_REQUEST, "인증이 완료되지 않았습니다."),
    ALREADY_VERIFIED("SMS402", HttpStatus.BAD_REQUEST, "이미 완료된 인증입니다."),

    // Medication
    MEDICATION_NOT_FOUND("MEDICATION400", HttpStatus.BAD_REQUEST, "약 정보가 존재하지 않습니다."),


    // Medication Schedule
    MEDICATION_SCHEDULE_NOT_FOUND("MEDSCHEDULE400", HttpStatus.BAD_REQUEST, "해당 복약 스케줄이 존재하지 않습니다.")
    ;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

}
