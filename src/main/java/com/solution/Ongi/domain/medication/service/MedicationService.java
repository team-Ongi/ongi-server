package com.solution.Ongi.domain.medication.service;

import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.enums.MealType;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.dto.CreateFixedTimeMedicationRequest;
import com.solution.Ongi.domain.medication.dto.CreateMealBasedMedicationRequest;
import com.solution.Ongi.domain.medication.dto.CreateMedicationResponse;
import com.solution.Ongi.domain.medication.dto.MedicationResponse;
import com.solution.Ongi.domain.medication.dto.UpdateFixedTimeMedicationRequest;
import com.solution.Ongi.domain.medication.dto.UpdateMealBasedMedicationRequest;
import com.solution.Ongi.domain.medication.enums.IntakeTiming;
import com.solution.Ongi.domain.medication.enums.MedicationType;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.solution.Ongi.domain.medication.enums.IntakeTiming.AFTER_MEAL;
import static com.solution.Ongi.domain.medication.enums.IntakeTiming.BEFORE_MEAL;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicationService {

    private final UserService userService;
    private final MedicationRepository medicationRepository;
    private final DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MealRepository mealRepository;

    // 정시 복용 약 등록
    public CreateMedicationResponse createFixedTimeMedication(String loginId, CreateFixedTimeMedicationRequest request){
        // 유저 검증
        User user=userService.getUserByLoginIdOrThrow(loginId);

        // 약 정보 등록
        Medication medication = Medication.builder()
            .medicationName(request.medicationName())
            .medicationType(MedicationType.FIXED_TIME)
            .medicationTimes(request.timeList().stream()
                .map(time -> LocalTime.parse(time, timeFormatter))
                .toList()
            )
            .remindAfterMinutes(request.remindAfterMinutes())
            .user(user)
            .build();
        medicationRepository.save(medication);

        // 약 스케줄 등록
        LocalDate today = LocalDate.now();
        List<MedicationSchedule> schedules = request.timeList().stream()
                .map(timeStr -> LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm")))
                .map(time -> MedicationSchedule.builder()
                        .medication(medication)
                        .scheduledDate(today)
                        .scheduledTime(time)
                        .isTaken(false)
                        .build()
                ).toList();
        medicationScheduleRepository.saveAll(schedules);

        return new CreateMedicationResponse(medication.getId());
    }

    // 식전/식후 복용 약 등록
    public CreateMedicationResponse createMealBasedMedication(String loginId, CreateMealBasedMedicationRequest request){
        // 유저 정보 검증
        User user=userService.getUserByLoginIdOrThrow(loginId);

        // 약 정보 등록
        Medication medication = Medication.builder()
            .medicationName(request.medicationName())
            .medicationType(MedicationType.MEAL_BASED)
            .intakeTiming(request.intakeTiming())
            .mealTypes(request.mealTypeList())
            .remindAfterMinutes(request.remindAfterMinutes())
            .user(user)
            .build();
        medicationRepository.save(medication);

        // 2. 스케줄 생성
        LocalDate today = LocalDate.now();
        List<MedicationSchedule> schedules = request.mealTypeList().stream()
                .map(mealType -> {
                    Meal meal = mealRepository
                            .findByUserAndMealType(user, mealType)
                            .orElseThrow(() -> new IllegalArgumentException("식사 시간이 등록되지 않았습니다."));

                    int offset = 30;
                    LocalTime scheduledTime = switch (request.intakeTiming()) {
                        case AFTER_MEAL -> meal.getMealTime().plusMinutes(offset);
                        case BEFORE_MEAL -> meal.getMealTime().minusMinutes(offset);
                    };

                    return MedicationSchedule.builder()
                            .medication(medication)
                            .scheduledDate(LocalDate.now())
                            .scheduledTime(scheduledTime)
                            .isTaken(false)
                            .build();
                })
                .toList();


        medicationScheduleRepository.saveAll(schedules);

        return new CreateMedicationResponse(medication.getId());
    }

    public void updateFixedTimeMedication(String loginId, Long medicationId, UpdateFixedTimeMedicationRequest request) {
        Medication medication = getAuthorizedMedication(loginId, medicationId);

        List<LocalTime> timeList = request.timeList().stream()
            .map(LocalTime::parse)
            .toList();

        medication.updateFixedTime(request.medicationName(), timeList);
    }

    public void updateMealBasedMedication(String loginId, Long medicationId, UpdateMealBasedMedicationRequest request) {
        Medication medication = getAuthorizedMedication(loginId, medicationId);

        medication.updateMealBased(
            request.medicationName(),
            request.intakeTiming(),
            request.mealTypes(),
            request.remindAfterMinutes()
        );
    }

    // 유저의 Medication 전체 조회
    public List<MedicationResponse> getAllMedication(String loginId){
        User user = userService.getUserByLoginIdOrThrow(loginId);
        List<Medication> medications = medicationRepository.findAllByUserId(user.getId());
        List<MedicationResponse> result = medications.stream()
            .map(medication ->
                MedicationResponse.from(
                    medication,
                    medication.getMedicationTimes(),
                    medication.getMealTypes()
                )
            )
            .toList();
        return result;
    }

    // Medication 삭제
    public void deleteMedication(String loginId, Long medicationId){
        Medication medication = getAuthorizedMedication(loginId, medicationId);
        medicationScheduleRepository.deleteByMedication(medication);
        medicationRepository.delete(medication);
    }

    private Medication getAuthorizedMedication(String loginId, Long medicationId) {
        User user = userService.getUserByLoginIdOrThrow(loginId);

        Medication medication=medicationRepository.findById(medicationId)
            .orElseThrow(()->new GeneralException(ErrorStatus.MEDICATION_NOT_FOUND));

        if (!medication.getUser().getId().equals(user.getId())){
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        return medication;
    }

}
