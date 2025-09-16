package com.matchday.modules.match.api.dto.projection;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.domain.enums.SportsType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 팀의 확정된 매치 목록 조회용 Projection 구현체
 */
@Getter
@AllArgsConstructor
public class TeamConfirmedMatchProjectionImpl implements TeamConfirmedMatchProjection {
    
    private final Long matchId;
    private final String opponentTeamName;
    private final String myTeamRole;
    private final City city;
    private final District district;
    private final String placeName;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Integer fee;
    private final MatchSize matchSize;
    private final SportsType sportsType;
    private final MatchStatus status;
    private final Integer homeScore;
    private final Integer awayScore;
}