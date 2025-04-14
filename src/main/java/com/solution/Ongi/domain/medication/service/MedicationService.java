package com.solution.Ongi.domain.medication.service;

import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.dto.CreateMedicationRequest;
import com.solution.Ongi.domain.medication.repository.MedicationRepository;
import com.solution.Ongi.domain.user.User;
import com.solution.Ongi.domain.user.repository.UserRepository;
import com.solution.Ongi.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final MedicationRepository medicationRepository;
    private final DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");

    //Meal 생성
    public Medication createMedication(Long userId, CreateMedicationRequest createMedicationRequest){
        User user=userService.getUserByIdOrThrow(userId);

        Medication medication=Medication.builder()
                .medication_title(createMedicationRequest.getMedication_title())
                .medication_time(LocalTime.parse(createMedicationRequest.getMedication_time(),timeFormatter))
                .user(user)
                .build();

        return medicationRepository.save(medication);
    }

    //유저의 Meal 전체 조회
    public List<Medication> getAllMedication(Long userId){
        userService.getUserByIdOrThrow(userId);
        return medicationRepository.findByUserId(userId);
    }

    //Meal 삭제
    public void deleteMedication(Long medication_id){
        Medication medication=medicationRepository.findById(medication_id)
                .orElseThrow(()->new RuntimeException("약이 존재하지 않습니다."));
        medicationRepository.delete(medication);
    }
}
