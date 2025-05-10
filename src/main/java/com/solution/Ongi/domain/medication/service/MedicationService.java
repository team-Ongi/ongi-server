package com.solution.Ongi.domain.medication.service;

import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.dto.CreateFixedTimeMedicationRequest;
import com.solution.Ongi.domain.medication.dto.CreateMealBasedMedicationRequest;
import com.solution.Ongi.domain.medication.dto.CreateMedicationResponse;
import com.solution.Ongi.domain.medication.dto.MedicationResponseDTO;
import com.solution.Ongi.domain.medication.dto.UpdateFixedTimeMedicationRequest;
import com.solution.Ongi.domain.medication.dto.UpdateMealBasedMedicationRequest;
import com.solution.Ongi.domain.medication.enums.MedicationType;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.service.UserService;
import com.solution.Ongi.global.response.code.ErrorStatus;
import com.solution.Ongi.global.response.exception.GeneralException;
import java.time.LocalTime;
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
    private final DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");

    public CreateMedicationResponse createFixedTimeMedication(String loginId, CreateFixedTimeMedicationRequest request){
        User user=userService.getUserByLoginIdOrThrow(loginId);

        Medication medication = Medication.builder()
            .medicationTitle(request.title())
            .type(MedicationType.FIXED_TIME)
            .medicationTimes(request.timeList().stream()
                .map(time -> LocalTime.parse(time, timeFormatter))
                .toList()
            )
            .user(user)
            .build();

        medicationRepository.save(medication);

        return new CreateMedicationResponse(medication.getId());
    }

    public CreateMedicationResponse createMealBasedMedication(String loginId, CreateMealBasedMedicationRequest request){
        User user=userService.getUserByLoginIdOrThrow(loginId);

        Medication medication = Medication.builder()
            .medicationTitle(request.title())
            .type(MedicationType.MEAL_BASED)
            .intakeTiming(request.intakeTiming())
            .mealTypes(request.mealTypeList())
            .remindAfterMinutes(request.remindAfterMinutes())
            .user(user)
            .build();

        medicationRepository.save(medication);

        return new CreateMedicationResponse(medication.getId());
    }

    public void updateFixedTimeMedication(String loginId, Long medicationId, UpdateFixedTimeMedicationRequest request) {
        Medication medication = getAuthorizedMedication(loginId, medicationId);

        List<LocalTime> timeList = request.timeList().stream()
            .map(LocalTime::parse)
            .toList();

        medication.updateFixedTime(request.title(), timeList);
    }

    public void updateMealBasedMedication(String loginId, Long medicationId, UpdateMealBasedMedicationRequest request) {
        Medication medication = getAuthorizedMedication(loginId, medicationId);

        medication.updateMealBased(
            request.title(),
            request.intakeTiming(),
            request.mealTypes(),
            request.remindAfterMinutes()
        );
    }

    // 유저의 Medication 전체 조회
    public List<MedicationResponseDTO> getAllMedication(String loginId){
        User user = userService.getUserByLoginIdOrThrow(loginId);
        List<Medication> medications = medicationRepository.findAllByUserId(user.getId());
        List<MedicationResponseDTO> result = medications.stream()
            .map(medication ->
                MedicationResponseDTO.from(
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
