package com.matchday.modules.match.domain;

import com.matchday.common.entity.BaseEntity;
import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.domain.enums.SportsType;
import com.matchday.modules.match.exception.MatchControllerAdvice;
import com.matchday.modules.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Match extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Team homeTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private City city;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private District district;
    @Column(nullable = false)
    private String placeName;
    private String zipCode;

    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private LocalTime startTime;
    @Column(nullable = false)
    private LocalTime endTime;

    private Integer fee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchSize matchSize;

    private String description;

    private MatchStatus status;

    private String homeColor;
    private String awayColor;

    @Column(nullable = false)
    private Boolean hasBall;

    private Integer homeScore;
    private Integer awayScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportsType sportsType;

    public static Match createMatch(Team homeTeam, City city, District district, String placeName,
                                   LocalDate date, LocalTime startTime, LocalTime endTime,
                                   Integer fee, MatchSize matchSize, String zipCode, String homeColor,
                                   Boolean hasBall, SportsType sportsType, String notes) {
        Match match = new Match();
        match.homeTeam = homeTeam;
        match.city = city;
        match.district = district;
        match.placeName = placeName;
        match.date = date;
        match.startTime = startTime;
        match.endTime = endTime;
        match.fee = fee;
        match.matchSize = matchSize;
        match.zipCode = zipCode;
        match.homeColor = homeColor;
        match.hasBall = hasBall;
        match.sportsType = sportsType;
        match.description = notes;
        match.status = MatchStatus.PENDING;
        
        match.validateMatchDateTime();
        
        return match;
    }

    // 매치 점수 업데이트 (확정된 매치에서만 가능)
    public void updateScore(Integer homeScore, Integer awayScore) {
        if (homeScore < 0 || awayScore < 0) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_INVALID_SCORE);
        }
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    // 매치 완료 (점수가 입력된 경우)
    public void completeMatch() {
        if (this.homeScore == null || this.awayScore == null) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_SCORE_REQUIRED);
        }
        this.status = MatchStatus.COMPLETED;
    }

    // 매치 수락(대기 상태에서만 가능)
    public void acceptMatch() {
        if (!this.status.equals(MatchStatus.PENDING)) {
            throw new MatchControllerAdvice(ResponseCode._BAD_REQUEST);
        }
        this.status = MatchStatus.CONFIRMED;
    }

    // 매치 취소(대기 상태에서만 가능)
    public void cancelMatch() {
        if (!this.status.equals(MatchStatus.PENDING)) {
            throw new MatchControllerAdvice(ResponseCode._BAD_REQUEST);
        }
        this.status = MatchStatus.CANCELED;
    }

    // 매치 신청 가능 여부
    public boolean isAvailableForApplication() {
        return this.status.equals(MatchStatus.PENDING) &&
               (this.date.isAfter(LocalDate.now()) ||
               (this.date.isEqual(LocalDate.now()) && this.startTime.isAfter(LocalTime.now())));
    }

    // 올바른 시각인지 확인
    private void validateMatchDateTime() {
        LocalDateTime matchDateTime = LocalDateTime.of(date, startTime);

        if (matchDateTime.isBefore(LocalDateTime.now())) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_INVALID_DATE);
        }
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_INVALID_TIME);
        }
    }

    // 대기 중에만 수정 가능
    public void updateMatchInfo(City city, District district, String placeName, LocalDate date,
                               LocalTime startTime, LocalTime endTime, Integer fee,
                               String homeColor, String awayColor, Boolean hasBall) {
        if (!this.status.equals(MatchStatus.PENDING)) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_ALREADY_COMPLETED);
        }
        
        this.city = city;
        this.district = district;
        this.placeName = placeName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fee = fee;
        this.homeColor = homeColor;
        this.awayColor = awayColor;
        this.hasBall = hasBall;
        
        validateMatchDateTime();
    }
}
