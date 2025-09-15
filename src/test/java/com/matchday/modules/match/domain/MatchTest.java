package com.matchday.modules.match.domain;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.domain.enums.SportsType;
import com.matchday.modules.match.exception.MatchControllerAdvice;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

class MatchTest {

    @Test
    @DisplayName("매치 생성 성공")
    void createMatch_Success() {
        // given
        Team homeTeam = createTestTeam();
        City city = City.SEOUL;
        District district = District.SEOUL_GANGNAM;
        String placeName = "서울월드컵경기장";
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        Integer fee = 50000;
        MatchSize matchSize = MatchSize.ELEVEN;
        String zipCode = "12345";
        String homeColor = "#FFFFFF";
        Boolean hasBall = true;
        SportsType sportsType = SportsType.SOCCER;
        String notes = "테스트 매치입니다.";
        
        // when
        Match match = Match.createMatch(homeTeam, city, district, placeName, date, startTime, endTime,
                fee, matchSize, zipCode, homeColor, hasBall, sportsType, notes);
        
        // then
        assertThat(match.getHomeTeam()).isEqualTo(homeTeam);
        assertThat(match.getCity()).isEqualTo(city);
        assertThat(match.getDistrict()).isEqualTo(district);
        assertThat(match.getPlaceName()).isEqualTo(placeName);
        assertThat(match.getDate()).isEqualTo(date);
        assertThat(match.getStartTime()).isEqualTo(startTime);
        assertThat(match.getEndTime()).isEqualTo(endTime);
        assertThat(match.getFee()).isEqualTo(fee);
        assertThat(match.getMatchSize()).isEqualTo(matchSize);
        assertThat(match.getZipCode()).isEqualTo(zipCode);
        assertThat(match.getHomeColor()).isEqualTo(homeColor);
        assertThat(match.getHasBall()).isEqualTo(hasBall);
        assertThat(match.getSportsType()).isEqualTo(sportsType);
        assertThat(match.getDescription()).isEqualTo(notes);
    }

    @Test
    @DisplayName("과거 날짜로 매치 생성 시 예외 발생")
    void createMatch_PastDate_ThrowsException() {
        // given
        Team homeTeam = createTestTeam();
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        
        // when & then
        assertThatThrownBy(() -> Match.createMatch(homeTeam, City.SEOUL, District.SEOUL_GANGNAM,
                "테스트장소", pastDate, startTime, endTime, 50000, MatchSize.ELEVEN,
                "12345", "#FFFFFF", true, SportsType.SOCCER, "테스트"))
            .isInstanceOf(MatchControllerAdvice.class);
    }

    @Test
    @DisplayName("시작 시간이 종료 시간보다 늦을 때 예외 발생")
    void createMatch_StartTimeAfterEndTime_ThrowsException() {
        // given
        Team homeTeam = createTestTeam();
        LocalDate futureDate = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(16, 0);
        LocalTime endTime = LocalTime.of(14, 0);
        
        // when & then
        assertThatThrownBy(() -> Match.createMatch(homeTeam, City.SEOUL, District.SEOUL_GANGNAM,
                "테스트장소", futureDate, startTime, endTime, 50000, MatchSize.ELEVEN,
                "12345", "#FFFFFF", true, SportsType.SOCCER, "테스트"))
            .isInstanceOf(MatchControllerAdvice.class);
    }

    @Test
    @DisplayName("시작 시간과 종료 시간이 같을 때 예외 발생")
    void createMatch_StartTimeEqualsEndTime_ThrowsException() {
        // given
        Team homeTeam = createTestTeam();
        LocalDate futureDate = LocalDate.now().plusDays(1);
        LocalTime sameTime = LocalTime.of(14, 0);
        
        // when & then
        assertThatThrownBy(() -> Match.createMatch(homeTeam, City.SEOUL, District.SEOUL_GANGNAM,
                "테스트장소", futureDate, sameTime, sameTime, 50000, MatchSize.ELEVEN,
                "12345", "#FFFFFF", true, SportsType.SOCCER, "테스트"))
            .isInstanceOf(MatchControllerAdvice.class);
    }

    @Test
    @DisplayName("매치 점수 업데이트 성공")
    void updateScore_Success() {
        // given
        Match match = createTestMatch();
        
        // when
        match.updateScore(2, 1);
        
        // then
        assertThat(match.getHomeScore()).isEqualTo(2);
        assertThat(match.getAwayScore()).isEqualTo(1);
    }

    @Test
    @DisplayName("음수 점수로 업데이트 시 예외 발생")
    void updateScore_NegativeScore_ThrowsException() {
        // given
        Match match = createTestMatch();
        
        // when & then
        assertThatThrownBy(() -> match.updateScore(-1, 0))
            .isInstanceOf(MatchControllerAdvice.class);
    }

    @Test
    @DisplayName("매치 완료 성공")
    void completeMatch_Success() {
        // given
        Match match = createTestMatch();
        match.updateScore(2, 1);
        
        // when
        match.completeMatch();
        
        // then
        assertThat(match.getStatus().equals(MatchStatus.COMPLETED)).isTrue();
    }

    @Test
    @DisplayName("점수 없이 매치 완료 시 예외 발생")
    void completeMatch_WithoutScore_ThrowsException() {
        // given
        Match match = createTestMatch();
        
        // when & then
        assertThatThrownBy(() -> match.completeMatch())
            .isInstanceOf(MatchControllerAdvice.class);
    }

    @Test
    @DisplayName("미래 매치는 신청 가능")
    void isAvailableForApplication_FutureMatch_ReturnsTrue() {
        // given
        Match match = createTestMatch();
        
        // when & then
        assertThat(match.isAvailableForApplication()).isTrue();
    }

    private Team createTestTeam() {
        return Team.createTeam(
            "테스트팀", 
            "테스트 팀 설명", 
            TeamType.CLUB,
            City.SEOUL, 
            District.SEOUL_GANGNAM, 
            "#FF0000",
            true, 
            GroupGender.MALE, 
            30,
            "국민은행",
            "123-456-789",
            null
        );
    }

    private Match createTestMatch() {
        return Match.createMatch(
            createTestTeam(),
            City.SEOUL,
            District.SEOUL_GANGNAM,
            "테스트장소",
            LocalDate.now().plusDays(1),
            LocalTime.of(14, 0),
            LocalTime.of(16, 0),
            50000,
            MatchSize.ELEVEN,
            "12345",
            "#FFFFFF",
            true,
            SportsType.SOCCER,
            "테스트 매치"
        );
    }
}