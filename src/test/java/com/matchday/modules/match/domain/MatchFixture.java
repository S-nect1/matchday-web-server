package com.matchday.modules.match.domain;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.SportsType;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.team.domain.TeamFixture;

import java.time.LocalDate;
import java.time.LocalTime;

public class MatchFixture {
    
    public static Match defaultMatch() {
        return createMatch(TeamFixture.defaultTeam());
    }
    
    public static Match matchWithTeam(Team homeTeam) {
        return createMatch(homeTeam);
    }
    
    private static Match createMatch(Team homeTeam) {
        return Match.createMatch(
                homeTeam,
                City.SEOUL,
                District.SEOUL_GANGNAM,
                "테스트 경기장",
                LocalDate.now().plusDays(7),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                100000,
                MatchSize.ELEVEN,
                "12345",
                "#FF0000",
                true,
                SportsType.SOCCER,
                "테스트 매치"
        );
    }
}