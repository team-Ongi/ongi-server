package com.solution.Ongi.domain.schedule.controller;

import com.solution.Ongi.domain.schedule.dto.CalendarDayStatusRequest;
import com.solution.Ongi.domain.schedule.dto.CalendarDayStatusResponse;
import com.solution.Ongi.domain.schedule.service.CalendarService;
import com.solution.Ongi.global.response.ApiResponse;
import com.solution.Ongi.global.response.code.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/missed")
    @Operation(
            summary = "한 달 단위 누락 스케줄 조회",
            description = "요청한 년/월의 식사(Meal) 및 복약(Medication) 미완료 일자와 상태 반환합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "월별 누락 스케줄 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalendarDayStatusResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "년/월 값이 올바르지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 경우", content = @Content)
    public ResponseEntity<ApiResponse<List<CalendarDayStatusResponse>>> getMonthlyMissed(
            Authentication authentication,
            @RequestBody CalendarDayStatusRequest request){
        YearMonth ym=YearMonth.of(request.year(),request.month());
        LocalDate startDate=ym.atDay(1);
        LocalDate endDate=ym.atEndOfMonth();

        List<CalendarDayStatusResponse> result=
                calendarService.getMissedStatus(authentication.getName(),startDate,endDate);

        return ResponseEntity.ok(ApiResponse.success(result, SuccessStatus.SUCCESS_200));
    }
}
