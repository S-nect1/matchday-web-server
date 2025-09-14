package com.matchday.modules.match.domain;

import com.matchday.common.entity.BaseEntity;
import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.SportsType;
import com.matchday.modules.team.domain.Team;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Match extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team homeTeam;

    @Enumerated(EnumType.STRING)
    private City city;
    @Enumerated(EnumType.STRING)
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

    private Integer homeScore;
    private Integer awayScore;

    private SportsType sportsType;
}
