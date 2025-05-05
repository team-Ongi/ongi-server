package com.solution.Ongi.domain.meal.dto;

public class UpdateMealScheduleStatusRequest {
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}