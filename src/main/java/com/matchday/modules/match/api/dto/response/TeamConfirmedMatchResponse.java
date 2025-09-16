package com.matchday.modules.match.api.dto.response;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.api.dto.projection.TeamConfirmedMatchProjection;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.domain.enums.SportsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 팀의 확정된 매치 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamConfirmedMatchResponse {
    
    private Long matchId;
    private String opponentTeamName;
    private String myTeamRole;
    private City city;
    private District district;
    private String placeName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer fee;
    private MatchSize matchSize;
    private SportsType sportsType;
    private MatchStatus status;
    private Integer homeScore;
    private Integer awayScore;
    
    public static TeamConfirmedMatchResponse of(TeamConfirmedMatchProjection projection) {
        return TeamConfirmedMatchResponse.builder()
                .matchId(projection.getMatchId())
                .opponentTeamName(projection.getOpponentTeamName())
                .myTeamRole(projection.getMyTeamRole())
                .city(projection.getCity())
                .district(projection.getDistrict())
                .placeName(projection.getPlaceName())
                .date(projection.getDate())
                .startTime(projection.getStartTime())
                .endTime(projection.getEndTime())
                .fee(projection.getFee())
                .matchSize(projection.getMatchSize())
                .sportsType(projection.getSportsType())
                .status(projection.getStatus())
                .homeScore(projection.getHomeScore())
                .awayScore(projection.getAwayScore())
                .build();
    }
}