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
    USER_NOT_FOUND("USER404", HttpStatus.NOT_FOUND, "해당 Id는 존재하지 않습니다."),
    ALREADY_EXIST_USER("USER409", HttpStatus.CONFLICT, "이미 존재하는 UserId입니다."),
    INVALID_PASSWORD("USER401", HttpStatus.UNAUTHORIZED, "비밀번호가 유효하지 않습니다"),

    // Auth
    UNAUTHORIZED_ACCESS("AUTH403", HttpStatus.FORBIDDEN, "해당 리소스에 접근할 권한이 없습니다."),
    INVALID_TOKEN("AUTH401", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_MISMATCH("AUTH401", HttpStatus.UNAUTHORIZED, "일치하지 않는 토큰입니다"),
    LOGIN_ID_NOT_FOUND("LOGIN404", HttpStatus.NOT_FOUND, "해당 ID가 존재하지 않습니다."),
    SAME_AS_OLD_PASSWORD("PASSWORD409", HttpStatus.CONFLICT, "새 비밀번호는 이전과 같을 수 없습니다."),

    // Sms
    VERIFICATION_CODE_NOT_FOUND("SMS404", HttpStatus.NOT_FOUND, "해당 번호로 인증 요청이 없습니다."),
    VERIFICATION_NOT_COMPLETED("SMS400", HttpStatus.BAD_REQUEST, "인증이 완료되지 않았습니다."),
    VERIFICATION_EXPIRED("SMS400", HttpStatus.BAD_REQUEST, "인증번호 유효 시간이 만료되었습니다."),
    VERIFICATION_CODE_MISMATCH("SMS400", HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),



    // Medication
    MEDICATION_NOT_FOUND("MEDICATION404", HttpStatus.NOT_FOUND, "약 정보가 존재하지 않습니다."),

    // Medication Schedule
    MEDICATION_SCHEDULE_NOT_FOUND("MEDICATION_SCHEDULE404", HttpStatus.NOT_FOUND, "해당 복약 스케줄이 존재하지 않습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}


