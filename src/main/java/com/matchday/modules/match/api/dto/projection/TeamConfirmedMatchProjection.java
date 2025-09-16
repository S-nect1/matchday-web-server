package com.matchday.modules.match.api.dto.projection;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.domain.enums.SportsType;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 팀의 확정된 매치 목록 조회용 Projection 인터페이스
 * 확정된 매치(CONFIRMED, COMPLETED)만 포함
 */
public interface TeamConfirmedMatchProjection {
    
    Long getMatchId();
    String getOpponentTeamName();
    String getMyTeamRole(); // "HOME" 또는 "AWAY"
    City getCity();
    District getDistrict();
    String getPlaceName();
    LocalDate getDate();
    LocalTime getStartTime();
    LocalTime getEndTime();
    Integer getFee();
    MatchSize getMatchSize();
    SportsType getSportsType();
    MatchStatus getStatus();
    Integer getHomeScore();
    Integer getAwayScore();
}