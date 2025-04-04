package com.solution.Ongi.domain.user.enums;

import lombok.Getter;

@Getter
public enum AlertInterval {
    MINUTES_30(30),
    MINUTES_60(60);

    private final int value;

    AlertInterval(int value) {
        this.value = value;
    }
}
