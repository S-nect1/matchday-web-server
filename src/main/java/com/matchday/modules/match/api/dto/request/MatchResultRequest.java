package com.matchday.modules.match.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MatchResultRequest {

    @NotNull(message = "홈팀 점수는 필수입니다.")
    @PositiveOrZero(message = "홈팀 점수는 0 이상이어야 합니다.")
    private Integer homeScore;

    @NotNull(message = "어웨이팀 점수는 필수입니다.")
    @PositiveOrZero(message = "어웨이팀 점수는 0 이상이어야 합니다.")
    private Integer awayScore;
}