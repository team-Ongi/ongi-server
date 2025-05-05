package com.solution.Ongi.domain.medication.service;

import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.dto.MedicationScheduleResponse;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationStatusRequest;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationStatusResponse;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationScheduleService {

    private final MedicationScheduleRepository scheduleRepository;
    private final UserService userService;

    @Transactional
    public UpdateMedicationStatusResponse updateIsTaken(String loginId, Long scheduleId, UpdateMedicationStatusRequest request) {
        userService.getUserByLoginIdOrThrow(loginId);
        MedicationSchedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEDICATION_SCHEDULE_NOT_FOUND));

        // 본인 schedule인지 확인
        if (!schedule.getMedication().getUser().getLoginId().equals(loginId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        if (request.isTaken()) {
            schedule.markAsTaken();
        } else {
            schedule.markAsNotTaken(request.reason(), request.remindAfterMinutes());
        }

        return new UpdateMedicationStatusResponse(
            schedule.getId(),
            schedule.isTaken(),
            request.isTaken() ? "복용 완료로 처리되었습니다." : "복용하지 않음으로 처리되었습니다."
        );
    }

    public List<MedicationScheduleResponse> getSchedulesByDate(String loginId, LocalDate date) {
        User user = userService.getUserByLoginIdOrThrow(loginId);

        List<MedicationSchedule> schedules = scheduleRepository.findByUserAndDate(user.getId(), date);

        return schedules.stream()
            .map(MedicationScheduleResponse::from)
            .toList();
    }

    public List<MedicationScheduleResponse> getSchedulesByDateRange(String loginId, LocalDate startDate, LocalDate endDate) {
        User user = userService.getUserByLoginIdOrThrow(loginId);

        List<MedicationSchedule> schedules = scheduleRepository.findByUserAndDateRange(user.getId(), startDate, endDate);

        return schedules.stream()
            .map(MedicationScheduleResponse::from)
            .toList();
    }

    public List<MedicationScheduleResponse> getNotTakenMedicationSchedules(String loginId, LocalDate date) {
        User user = userService.getUserByLoginIdOrThrow(loginId);
        List<MedicationSchedule> schedules = scheduleRepository.findNotTakenByUserAndDate(user.getId(), date);
        return schedules.stream()
            .map(MedicationScheduleResponse::from)
            .toList();
    }

}
