package com.solution.Ongi.domain.user.dto;

import com.solution.Ongi.domain.user.enums.AlertInterval;
import com.solution.Ongi.domain.user.enums.RelationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SignupRequest(
    @NotNull
    @NotEmpty
    String loginId,
    @NotNull
    @NotEmpty
    String password,
    @NotNull
    @NotEmpty
    String guardianName,
    @NotNull
    @NotEmpty
    String guardianPhone,
    @NotNull
    @NotEmpty
    String seniorName,
    @NotNull
    @NotEmpty
    Integer seniorAge,
    @NotNull
    @NotEmpty
    String seniorPhone,
    @NotNull
    @NotEmpty
    RelationType relation,
    @NotNull
    @NotEmpty
    AlertInterval alertMax,
    @NotNull
    @NotEmpty
    Integer ignoreCnt,
    @NotNull
    Boolean pushAgreement,
    @NotNull
    Boolean voiceAgreement,
    @NotNull
    Boolean backgroundAgreement
) { }
