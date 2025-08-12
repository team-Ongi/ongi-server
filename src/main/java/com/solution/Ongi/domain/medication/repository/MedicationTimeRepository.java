package com.solution.Ongi.domain.medication.repository;

import com.solution.Ongi.domain.medication.Medication;
import com.solution.Ongi.domain.medication.MedicationTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicationTimeRepository extends JpaRepository<MedicationTime, Long> {
//    @Query(value = "DELETE FROM medication_time WHERE medication_id = :medicationId", nativeQuery = true)
//    void deleteAllByMedicationId(@Param("medicationId") Long medicationId);

    void deleteAllByMedication(Medication medication);
}
