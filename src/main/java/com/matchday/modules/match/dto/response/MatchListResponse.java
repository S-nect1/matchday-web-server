package com.matchday.modules.match.dto.response;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.SportsType;
import com.matchday.modules.match.dto.projection.MatchListProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchListResponse {
    private Long matchId;
    private String homeTeamName;
    private City city;
    private District district;
    private String placeName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer fee;
    private MatchSize matchSize;
    private SportsType sportsType;
    private Boolean isMatchable;
    private LocalDateTime createdDate;
    
    /**
     * Match 엔티티로부터 MatchListResponse 생성
     */
    public static MatchListResponse of(Match match) {
        return new MatchListResponse(
            match.getId(),
            match.getHomeTeam().getName(),
            match.getCity(),
            match.getDistrict(),
            match.getPlaceName(),
            match.getDate(),
            match.getStartTime(),
            match.getEndTime(),
            match.getFee(),
            match.getMatchSize(),
            match.getSportsType(),
            match.isAvailableForApplication(),
            match.getCreatedDate()
        );
    }
    
    /**
     * MatchListProjection으로부터 MatchListResponse 생성
     * PENDING 상태의 매치는 신청 가능하므로 isMatchable을 true로 설정
     */
    public static MatchListResponse of(MatchListProjection projection) {
        return new MatchListResponse(
            projection.getId(),
            projection.getHomeTeamName(),
            projection.getCity(),
            projection.getDistrict(),
            projection.getPlaceName(),
            projection.getDate(),
            projection.getStartTime(),
            projection.getEndTime(),
            projection.getFee(),
            projection.getMatchSize(),
            projection.getSportsType(),
            true, // PENDING 상태이므로 신청 가능
            projection.getCreatedDate()
        );
    }
}