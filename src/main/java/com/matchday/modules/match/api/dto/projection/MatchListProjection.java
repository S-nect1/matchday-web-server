package com.matchday.modules.match.api.dto.projection;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.SportsType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 매치 목록 조회를 위한 DTO projection 인터페이스
 * 필요한 필드만 선별적으로 조회하여 성능을 최적화
 */
public interface MatchListProjection {
    Long getId();
    String getHomeTeamName();
    City getCity();
    District getDistrict();
    String getPlaceName();
    LocalDate getDate();
    LocalTime getStartTime();
    LocalTime getEndTime();
    Integer getFee();
    MatchSize getMatchSize();
    SportsType getSportsType();
    LocalDateTime getCreatedDate();
}