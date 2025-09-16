package com.matchday.modules.match.api.dto.response;

import com.matchday.modules.match.domain.MatchApplication;
import com.matchday.modules.match.domain.enums.MatchApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchApplicationResponse {
    private Long applicationId;
    private Long matchId;
    private String message;
    private MatchApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime processedAt;
    
    public static MatchApplicationResponse of(MatchApplication application) {
        return new MatchApplicationResponse(
            application.getId(),
            application.getMatch().getId(),
            application.getMessage(),
            application.getStatus(),
            application.getCreatedAt(),
            application.getProcessedAt()
        );
    }
}