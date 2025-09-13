package com.matchday.match.domain;

import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.match.domain.enums.MatchSize;
import com.matchday.match.domain.enums.SportsType;
import com.matchday.team.domain.Team;
import com.matchday.team.domain.TeamFixture;

import java.time.LocalDate;
import java.time.LocalTime;
import java.lang.reflect.Field;

public class MatchFixture {
    
    public static Match defaultMatch() {
        return createMatch(TeamFixture.defaultTeam());
    }
    
    public static Match matchWithTeam(Team homeTeam) {
        return createMatch(homeTeam);
    }
    
    private static Match createMatch(Team homeTeam) {
        try {
            Match match = new Match();
            
            // Reflection을 사용하여 private 필드에 값 설정
            setField(match, "homeTeam", homeTeam);
            setField(match, "city", City.SEOUL);
            setField(match, "district", District.SEOUL_GANGNAM);
            setField(match, "placeName", "테스트 경기장");
            setField(match, "date", LocalDate.now().plusDays(7));
            setField(match, "startTime", LocalTime.of(14, 0));
            setField(match, "endTime", LocalTime.of(16, 0));
            setField(match, "fee", 100000);
            setField(match, "matchSize", MatchSize.ELEVEN);
            setField(match, "homeColor", "#FF0000");
            setField(match, "awayColor", "#0000FF");
            setField(match, "hasBall", true);
            setField(match, "sportsType", SportsType.SOCCER);
            
            return match;
        } catch (Exception e) {
            throw new RuntimeException("Match 생성 중 오류 발생", e);
        }
    }
    
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}