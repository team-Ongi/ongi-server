//package com.solution.Ongi.domain.medication.service;
//
//import com.solution.Ongi.domain.meal.Meal;
//import com.solution.Ongi.domain.meal.dto.CreateMealRequest;
//import com.solution.Ongi.domain.medication.Medication;
//import com.solution.Ongi.domain.medication.dto.CreateMedicationRequest;
//import com.solution.Ongi.domain.medication.repository.MedicationRepository;
//import com.solution.Ongi.domain.user.User;
//import com.solution.Ongi.domain.user.enums.AlertInterval;
//import com.solution.Ongi.domain.user.enums.RelationType;
//import com.solution.Ongi.domain.user.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.assertj.core.api.Assert;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class MedicationServiceTest {
//
//    @Autowired MedicationService medicationService;
//    @Autowired MedicationRepository medicationRepository;
//    @Autowired UserRepository userRepository;
//
//    private User testUser;
//
//    @BeforeEach
//    void setUser(){
//        testUser = User.builder()
//                .loginId("testUser")
//                .password("testPassword")
//                .guardianName("guardian")
//                .guardianPhone("010-1111-2222")
//                .seniorName("senior")
//                .seniorAge(80)
//                .seniorPhone("010-3333-4444")
//                .relation(RelationType.SON)
//                .alertMax(AlertInterval.MINUTES_30)
//                .build();
//        userRepository.save(testUser);
//    }
//
//    @Test
//    void createMedication() throws Exception {
//
//        //Given
//        CreateMedicationRequest request=CreateMedicationRequest.builder()
//                .medication_title("testMedication")
//                .medication_time("10:00")
//                .build();
//        //When
//        Medication medication=medicationService.createMedication(testUser.getId(), request);
//
//        //Then
//        Assertions.assertThat(medication.getMedication_title()).isEqualTo("testMedication");
//        Assertions.assertThat(medication.getMedication_time()).isEqualTo(LocalTime.of(10,0));
//    }
//
//    @Test
//    void getAllMedication() {
//
//        //Given
//        Medication medication1=Medication.builder()
//                .medication_title("medication1")
//                .medication_time(LocalTime.of(8,30))
//                .user(testUser)
//                .build();
//        medicationRepository.save(medication1);
//
//        Medication medication2=Medication.builder()
//                .medication_title("medication2")
//                .medication_time(LocalTime.of(12,30))
//                .user(testUser)
//                .build();
//        medicationRepository.save(medication2);
//
//        //When
//        List<Medication> medications=medicationService.getAllMedication(testUser.getId());
//
//        //Then
//        Assertions.assertThat(medications).hasSize(2);
//        Assertions.assertThat(medications.get(0).getMedication_title()).isEqualTo("medication1");
//
//    }
//
//    @Test
//    void deleteMedication() {
//
//        //Given
//        Medication medication=Medication.builder()
//                .medication_title("testMedication")
//                .medication_time(LocalTime.of(10,0))
//                .user(testUser)
//                .build();
//        Medication saved=medicationRepository.save(medication);
//
//        //When
//        medicationService.deleteMedication(saved.getId());
//
//        //Then
//        Assertions.assertThat(medicationRepository.findById(saved.getId())).isEmpty();
//    }
//}