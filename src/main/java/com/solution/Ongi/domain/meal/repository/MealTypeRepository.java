package com.solution.Ongi.domain.meal.repository;

import com.solution.Ongi.domain.meal.MealType;
import com.solution.Ongi.domain.medication.Medication;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MealTypeRepository extends JpaRepository<MealType, Long> {
    boolean existsByMedication(Medication medication);
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM meal_type WHERE medication_id = :medicationId", nativeQuery = true)
    void deleteAllByMedicationId(@Param("medicationId") Long medicationId);
}
