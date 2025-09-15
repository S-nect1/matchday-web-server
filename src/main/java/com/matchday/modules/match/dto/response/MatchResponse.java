package com.matchday.modules.match.dto.response;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.domain.enums.SportsType;
import com.matchday.modules.team.dto.response.TeamResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private Long matchId;
    private TeamResponse homeTeam;
    private TeamResponse awayTeam;
    private City city;
    private District district;
    private String placeName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer fee;
    private MatchSize matchSize;
    private String homeColor;
    private String awayColor;
    private Boolean hasBall;
    private SportsType sportsType;
    private Boolean isCompleted;
    private Integer homeScore;
    private Integer awayScore;
    private LocalDateTime createdDate;
    
    public static MatchResponse of(Match match) {
        return new MatchResponse(
            match.getId(),
            TeamResponse.from(match.getHomeTeam(), null),
            null, // awayTeam은 별도 서비스 로직에서 MatchApplication을 통해 조회
            match.getCity(),
            match.getDistrict(),
            match.getPlaceName(),
            match.getDate(),
            match.getStartTime(),
            match.getEndTime(),
            match.getFee(),
            match.getMatchSize(),
            match.getHomeColor(),
            match.getAwayColor(),
            match.getHasBall(),
            match.getSportsType(),
            match.getStatus().equals(MatchStatus.COMPLETED),
            match.getHomeScore(),
            match.getAwayScore(),
            match.getCreatedDate()
        );
    }
}