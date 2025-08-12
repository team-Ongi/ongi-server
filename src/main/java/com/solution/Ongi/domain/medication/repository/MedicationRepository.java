package com.solution.Ongi.domain.medication.repository;

import com.solution.Ongi.domain.medication.Medication;
import java.util.List;
import java.util.Optional;

import com.solution.Ongi.domain.medication.enums.MedicationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends JpaRepository<Medication,Long> {
    List<Medication> findAllByUserId(Long userId);
    List<Medication> findAllByUserIdAndMedicationType(Long userId, MedicationType medicationType);
    Optional<Medication> findById(Long id);
}
