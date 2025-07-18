package com.solution.Ongi.domain.user.repository.projection;

import java.time.LocalDate;

public interface NotTakenMealStatusProjection {
    LocalDate getDate();
    Boolean getStatus();
}
