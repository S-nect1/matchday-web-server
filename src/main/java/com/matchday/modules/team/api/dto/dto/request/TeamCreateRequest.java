package com.matchday.modules.team.api.dto.dto.request;

import com.matchday.common.entity.enums.ActivityArea;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TeamCreateRequest(
    @NotBlank(message = "팀 이름은 필수입니다.")
    @Size(max = 100, message = "팀 이름은 100자 이내여야 합니다.")
    String name,
    
    @Size(max = 500, message = "팀 설명은 500자 이내여야 합니다.")
    @NotBlank(message = "팀 설명은 필수입니다.")
    String description,
    
    @NotNull(message = "팀 유형은 필수입니다.")
    TeamType type,
    
    @Valid
    @NotNull(message = "활동지역은 필수입니다.")
    ActivityArea activityArea,

    @NotBlank(message = "유니폼 색상은 필수입니다.")
    String uniformColorHex,
    Boolean hasBall,
    
    @NotNull(message = "팀 성별은 필수입니다.")
    GroupGender gender,

    Integer memberLimit,
    
    @NotBlank(message = "은행명은 필수입니다.")
    String bankName,
    
    @NotBlank(message = "계좌번호는 필수입니다.")
    String bankAccount,
    
    String profileImageUrl
) {
}