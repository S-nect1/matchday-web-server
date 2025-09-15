package com.matchday.modules.team.api.dto.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TeamJoinRequest(
    @NotBlank(message = "초대코드는 필수입니다.")
    @Pattern(regexp = "^[0-9]{6}$", message = "초대코드는 6자리 숫자여야 합니다.")
    String inviteCode
    
//    Integer backNumber
) {
}