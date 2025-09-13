package com.matchday.team.domain;

import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.enums.GroupGender;
import com.matchday.team.domain.enums.TeamType;

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
}