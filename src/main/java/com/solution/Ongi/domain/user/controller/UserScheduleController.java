package com.solution.Ongi.domain.user.controller;

import com.solution.Ongi.domain.user.dto.UserSchedulesResponse;
import com.solution.Ongi.domain.user.service.UserScheduleService;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.ApiResponse;
import com.solution.Ongi.global.response.code.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-schedule")
public class UserScheduleController {

    private final UserService userService;
    private final UserScheduleService userScheduleService;

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

    // 유저의 약 && 식사 정보 조회
    @GetMapping("/all")
    @Operation(summary = "사용자의 모든 약 && 식사 정보 조회",
            description = "현재 로그인한 사용자의 전체 약 && 식사 정보를 조회합니다. ")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자의 모든 약 && 식사 정보 조회 성공", content = @Content(mediaType = "application/json",schema =@Schema(implementation =UserSchedulesResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "로그인 아이디가 존재하지 않음", content = @Content(mediaType = "application/json",schema =@Schema()))
    public ResponseEntity<ApiResponse<UserSchedulesResponse>>  getAllSchedules(Authentication authentication) {
        UserSchedulesResponse response = userScheduleService.getAllSchedules(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response,SuccessStatus.SUCCESS_200));
    }
}
