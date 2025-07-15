package com.solution.Ongi.domain.user.controller;

import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.ApiResponse;
import com.solution.Ongi.global.response.code.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-schedule")
public class UserScheduleController {

    private final UserService userService;

    @PostMapping("/ignore-cnt")
    @Operation(
            summary = "사용자 ignoreCnt 설정",
            description = """
            로그인된 사용자의 ignoreCnt를 지정된 값으로 설정합니다.<br><br>
            - 호출 시 파라미터로 전달된 `ignoreCnt` 값으로 덮어쓰고,<br>
            - 이후 `/user/me` API 등에서 확인 가능합니다.
           """
    )
    public ResponseEntity<ApiResponse<Long>> setIgnoreCnt(
            Authentication authentication,
            @RequestParam Integer ignoreCnt
    ){
        Long updateCnt= userService.setUserIgnoreCnt(authentication.getName(),ignoreCnt);
        return ResponseEntity.ok(ApiResponse.success(updateCnt, SuccessStatus.SUCCESS_200));
    }
}
