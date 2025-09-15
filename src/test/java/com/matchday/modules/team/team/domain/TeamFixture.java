package com.matchday.modules.team.team.domain;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamType;

public class TeamFixture {
    
    public static Team defaultTeam() {
        return Team.createTeam(
            "Default Team", 
            "테스트용 팀", 
            TeamType.CLUB,
            City.SEOUL, 
            District.SEOUL_GANGNAM, 
            "#FF0000",
            true, 
            GroupGender.MALE, 
            20,
            "국민은행", 
            "123-456-789", 
            null
        );
    }
    
    public static Team teamWithName(String name) {
        return Team.createTeam(
            name, 
            "테스트용 팀", 
            TeamType.CLUB,
            City.SEOUL, 
            District.SEOUL_GANGNAM, 
            "#FF0000",
            true, 
            GroupGender.MALE, 
            20,
            "국민은행", 
            "123-456-789", 
            null
        );
    }
    
    public static Team applicantTeam() {
        return Team.createTeam(
            "Applicant Team", 
            "신청하는 팀", 
            TeamType.CLUB,
            City.SEOUL, 
            District.SEOUL_SEOCHO, 
            "#0000FF",
            false, 
            GroupGender.MALE, 
            15,
            "신한은행", 
            "987-654-321", 
            null
        );
    }
    
    public static Team createFootballTeam(String name) {
        return Team.createTeam(
            name,
            "축구 팀",
            TeamType.CLUB,
            City.SEOUL,
            District.SEOUL_GANGNAM,
            "#FF0000",
            true,
            GroupGender.MIXED,
            22,
            "국민은행",
            "123-456-789",
            null
        );
    }
    
    public static Team createTeam(String name, City city, District district, 
                                 TeamType type, GroupGender gender) {
        return Team.createTeam(
            name,
            "테스트 팀",
            type,
            city,
            district,
            "#FF0000",
            true,
            gender,
            20,
            "국민은행",
            "123-456-789",
            null
        );
    }
}