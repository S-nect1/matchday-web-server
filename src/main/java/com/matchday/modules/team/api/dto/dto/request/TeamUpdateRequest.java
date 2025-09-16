package com.matchday.modules.team.api.dto.dto.request;

import com.matchday.common.entity.enums.ActivityArea;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record TeamUpdateRequest(
    @Size(max = 100, message = "팀 이름은 100자 이내여야 합니다.")
    String name,
    
    @Size(max = 500, message = "팀 설명은 500자 이내여야 합니다.")
    String description,
    
    @Valid
    ActivityArea activityArea,
    
    String uniformColorHex,
    Boolean hasBall,
    
    Integer memberLimit,
    
    String profileImageUrl
) {
}