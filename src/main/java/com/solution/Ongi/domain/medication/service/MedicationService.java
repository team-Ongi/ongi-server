package com.solution.Ongi.domain.medication.service;

import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.dto.CreateMedicationRequest;
import com.solution.Ongi.domain.medication.dto.MedicationDTO;
import com.solution.Ongi.domain.medication.dto.UpdateMedicationRequest;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.repository.UserRepository;
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
@Transactional(readOnly = true)
public class MedicationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final MedicationRepository medicationRepository;
    private final DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");

    // Medication 생성
    @Transactional
    public Medication createMedication(String userId, CreateMedicationRequest createMedicationRequest){
        User user=userService.getUserByLoginIdOrThrow(userId);

        Medication medication=Medication.builder()
                .medication_title(createMedicationRequest.getMedication_title())
                .medication_time(
                    createMedicationRequest.getTimeList().stream()
                        .map(time -> LocalTime.parse(time, timeFormatter))
                        .toList()
                )
                .user(user)
                .build();

        return medicationRepository.save(medication);
    }

    // 유저의 Medication 전체 조회
    public List<MedicationDTO> getAllMedication(String loginId){
        User user = userService.getUserByLoginIdOrThrow(loginId);
        List<Medication> medications = medicationRepository.findAllByUserId(user.getId());
        List<MedicationDTO> result = medications.stream()
            .map(MedicationDTO::new)
            .toList();
        return result;
    }

    // Medication 삭제
    @Transactional
    public void deleteMedication(String loginId, Long medication_id){
        User user = userService.getUserByLoginIdOrThrow(loginId);
        Medication medication=medicationRepository.findById(medication_id)
                .orElseThrow(()->new GeneralException(ErrorStatus.MEDICATION_NOT_FOUND));

        if (!medication.getUser().getId().equals(user.getId())){
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        medicationRepository.delete(medication);
    }

    // Medication 수정
    @Transactional
    public void updateMedication(String loginId, Long medicationId, UpdateMedicationRequest request) {
        User user = userService.getUserByLoginIdOrThrow(loginId);
        Medication medication = medicationRepository.findById(medicationId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEDICATION_NOT_FOUND));

        if (!medication.getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        List<LocalTime> convertedTimeList = request.timeList().stream()
            .map(LocalTime::parse) // "12:30" → LocalTime
            .toList();

        medication.update(request.title(), convertedTimeList);
    }

}
