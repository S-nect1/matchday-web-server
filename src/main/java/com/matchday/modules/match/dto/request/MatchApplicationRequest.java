package com.matchday.modules.match.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchApplicationRequest {
    
    @NotNull(message = "팀 ID는 필수입니다.")
    private Long teamId;
    
    @Size(max = 200, message = "신청 메시지는 200자를 초과할 수 없습니다.")
    private String message;
}