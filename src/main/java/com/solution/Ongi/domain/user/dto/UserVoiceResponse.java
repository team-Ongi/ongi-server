package com.solution.Ongi.domain.user.dto;

import java.util.List;

public record UserVoiceResponse(
        List<String> mealVoiceList,
        List<String> medicationVoiceList
) {
}
