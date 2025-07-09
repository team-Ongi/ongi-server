package com.solution.Ongi.domain.user.repository.projection;

import java.time.LocalDate;

public interface NotTakenStatsProjection {
    LocalDate getDate();
    Integer getNotTakenCount();
}
