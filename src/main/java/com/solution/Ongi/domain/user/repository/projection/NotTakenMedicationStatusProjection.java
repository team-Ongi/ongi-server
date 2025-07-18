package com.solution.Ongi.domain.user.repository.projection;

import java.time.LocalDate;

public interface NotTakenMedicationStatusProjection {
    LocalDate getDate();
    Integer getNotTakenCount();
}
