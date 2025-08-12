package com.solution.Ongi.domain.medication.service;

import com.solution.Ongi.domain.ai.dto.GenerateMedicationToSqsRequest;
import com.solution.Ongi.domain.meal.Meal;
import com.solution.Ongi.domain.meal.repository.MealRepository;
import com.solution.Ongi.domain.meal.repository.MealTypeRepository;
import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.MedicationSchedule;
import com.solution.Ongi.domain.medication.dto.*;
import com.solution.Ongi.domain.medication.enums.MedicationType;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.medication.repository.MedicationScheduleRepository;
import com.solution.Ongi.domain.medication.repository.MedicationTimeRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class MedicationService {

    private final UserService userService;
    private final MedicationRepository medicationRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MealRepository mealRepository;
    private final MealTypeRepository mealTypeRepository;
    private final MedicationTimeRepository medicationTimeRepository;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    // 정시 복용 약 등록
    public CreateMedicationResponse createFixedTimeMedication(String loginId, CreateFixedTimeMedicationRequest request) {
        // 유저 검증
        User user = userService.getUserByLoginIdOrThrow(loginId);

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
        LocalDate today = LocalDate.now(KST);
        List<MedicationSchedule> schedules = request.timeList().stream()
                .map(timeStr -> LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm")))
                .map(time -> MedicationSchedule.builder()
                        .medication(medication)
                        .scheduledDate(today)
                        .scheduledTime(time)
                        .status(false)
                        .build()
                ).toList();
        medicationScheduleRepository.saveAll(schedules);

        return new CreateMedicationResponse(medication.getId());
    }

    // 식전/식후 복용 약 등록
    public CreateMedicationResponse createMealBasedMedication(String loginId, CreateMealBasedMedicationRequest request) {
        // 유저 정보 검증
        User user = userService.getUserByLoginIdOrThrow(loginId);

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
        LocalDate today = LocalDate.now(KST);
        List<MedicationSchedule> schedules = request.mealTypeList().stream()
                .map(mealType -> {
                    Meal meal = mealRepository
                            .findByUserAndMealType(user, mealType)
                            .orElseThrow(() -> new GeneralException(ErrorStatus.MEAL_SCHEDULE_NOT_REGISTER));

                    int offset = 30;
                    LocalTime scheduledTime = switch (request.intakeTiming()) {
                        case AFTER_MEAL -> meal.getMealTime().plusMinutes(offset);
                        case BEFORE_MEAL -> meal.getMealTime().minusMinutes(offset);
                    };

                    return MedicationSchedule.builder()
                            .medication(medication)
                            .scheduledDate(today)
                            .scheduledTime(scheduledTime)
                            .status(false)
                            .build();
                })
                .toList();


        medicationScheduleRepository.saveAll(schedules);

        return new CreateMedicationResponse(medication.getId());
    }

    @Transactional
    public void updateFixedTimeMedication(String loginId, Long medicationId, UpdateFixedTimeMedicationRequest request) {
        // 유저 정보 가져오기
        userService.getUserByLoginIdOrThrow(loginId);

        // 기존 정보 삭제
        Medication medication = medicationRepository.findById(medicationId).orElseThrow(() -> new GeneralException(ErrorStatus.MEDICATION_NOT_FOUND));
        medicationScheduleRepository.deleteAllByMedication(medication);

        // 약 스케줄 등록
        LocalDate today = LocalDate.now(KST);
        List<MedicationSchedule> schedules = request.timeList().stream()
                .map(timeStr -> LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm")))
                .map(time -> MedicationSchedule.builder()
                        .medication(medication)
                        .scheduledDate(today)
                        .scheduledTime(time)
                        .status(false)
                        .build()
                ).toList();
        medicationScheduleRepository.saveAll(schedules);
    }

    @Transactional
    public void updateMealBasedMedication(String loginId, Long medicationId, UpdateMealBasedMedicationRequest request) {
        Medication medication = getAuthorizedMedication(loginId, medicationId);

        medication.updateMealBased(
                request.medicationName(),
                request.intakeTiming(),
                request.mealTypes(),
                request.remindAfterMinutes()
        );
    }

    // Medication 삭제
    public void deleteMedication(String loginId, Long medicationId) {
        Medication medication = getAuthorizedMedication(loginId, medicationId);
        medicationScheduleRepository.deleteByMedication(medication);
        medicationRepository.delete(medication);
    }

    private Medication getAuthorizedMedication(String loginId, Long medicationId) {
        User user = userService.getUserByLoginIdOrThrow(loginId);

        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEDICATION_NOT_FOUND));

        if (!medication.getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        return medication;
    }

    public void generateMedication(GenerateMedicationToSqsRequest response) {
        User user = userService.getUserByLoginIdOrThrow(response.loginId());

        deleteMealBasedMedicationRelation(user, MedicationType.MEAL_BASED);
        List<CreateMealBasedMedicationFromFastAPIRequest> medications = response.medicationData().medicationInfoFromFastAPIResponse();

        LocalDate today = LocalDate.now(KST);

        for (CreateMealBasedMedicationFromFastAPIRequest data : medications) {
            // 1. 약 정보 저장
            Medication medication = saveMedication(user, data);

            // 2. 스케줄 생성 및 저장
            List<MedicationSchedule> schedules = createMedicationSchedules(user, data, medication, today);
            medicationScheduleRepository.saveAll(schedules);
        }
    }

    @Transactional
    public void deleteMealBasedMedicationRelation(User user, MedicationType medicationType) {
        // 유저가 등록한 약 정보 모두 가져오기
        List<Medication> medicationList = medicationRepository.findAllByUserIdAndMedicationType(user.getId(), medicationType);

        // MealType삭제 -> MedicationSchedule 삭제 -> MedicationTime 삭제 -> user 삭제
        for (Medication medication : medicationList) {
            Long medicationId = medication.getId();
            mealTypeRepository.deleteAllByMedicationId(medicationId);
            medicationScheduleRepository.deleteAllByMedication(medication);
            medicationTimeRepository.deleteAllByMedication(medication);
            medicationRepository.deleteById(medication.getId());
        }
    }

    private Medication saveMedication(User user, CreateMealBasedMedicationFromFastAPIRequest data) {
        return medicationRepository.save(
                Medication.builder()
                        .medicationName(data.medicationName())
                        .medicationType(MedicationType.MEAL_BASED)
                        .intakeTiming(data.intakeTiming())
                        .mealTypes(data.mealTypeList())
                        .remindAfterMinutes(30)
                        .user(user)
                        .build()
        );
    }

    private List<MedicationSchedule> createMedicationSchedules(User user,
                                                               CreateMealBasedMedicationFromFastAPIRequest data,
                                                               Medication medication,
                                                               LocalDate date) {
        final int OFFSET_MINUTES = 30;
        return data.mealTypeList().stream()
                .map(mealType -> {
                    Meal meal = mealRepository.findByUserAndMealType(user, mealType)
                            .orElseThrow(() -> new GeneralException(ErrorStatus.MEAL_SCHEDULE_NOT_REGISTER));

                    LocalTime mealTime = meal.getMealTime();
                    LocalTime scheduledTime = switch (data.intakeTiming()) {
                        case AFTER_MEAL -> mealTime.plusMinutes(OFFSET_MINUTES);
                        case BEFORE_MEAL -> mealTime.minusMinutes(OFFSET_MINUTES);
                    };

                    return MedicationSchedule.builder()
                            .medication(medication)
                            .scheduledDate(date)
                            .scheduledTime(scheduledTime)
                            .status(false)
                            .build();
                })
                .toList();
    }
}
