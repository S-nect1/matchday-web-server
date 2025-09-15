package com.matchday.modules.match.api.dto.dto.projection;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.SportsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class MatchListProjectionImpl implements MatchListProjection {
    private Long id;
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
    private LocalDateTime createdDate;
}