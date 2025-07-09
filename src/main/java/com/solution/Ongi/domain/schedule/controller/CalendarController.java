package com.solution.Ongi.domain.schedule.controller;

import com.solution.Ongi.domain.schedule.dto.CalendarDayStatusResponse;
import com.solution.Ongi.domain.schedule.service.CalendarService;
import com.solution.Ongi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<List<CalendarDayStatusResponse>>> getMonthlyMissed(
            Authentication authentication,
            @Parameter(description = "조회할 년도")
            @RequestParam int year,
            @Parameter(description = "조회할 월 (1~12)")
            @RequestParam int month
    ){
        YearMonth ym=YearMonth.of(year,month);
        LocalDate startDate=ym.atDay(1);
        LocalDate endDate=ym.atEndOfMonth();

        List<CalendarDayStatusResponse> result=
                calendarService.getMissedStatus(authentication.getName(),startDate,endDate);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
