package com.matchday.modules.match.infrastructure;

import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.MatchApplication;
import com.matchday.modules.match.domain.enums.MatchApplicationStatus;
import com.matchday.modules.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchApplicationRepository extends JpaRepository<MatchApplication, Long> {
    
    // 특정 매치에 특정 팀의 신청이 있는지 확인
    boolean existsByMatchAndApplicantTeam(Match match, Team applicantTeam);
    
    // 특정 매치의 모든 신청 조회
    List<MatchApplication> findByMatchOrderByCreatedDateDesc(Match match);
    
    // 특정 매치의 특정 상태 신청들 조회
    List<MatchApplication> findByMatchAndStatus(Match match, MatchApplicationStatus status);
    
    // 특정 팀이 신청한 매치 신청들 조회
    List<MatchApplication> findByApplicantTeamOrderByCreatedDateDesc(Team applicantTeam);
    
    // 특정 매치와 팀의 신청 조회
    Optional<MatchApplication> findByMatchAndApplicantTeam(Match match, Team applicantTeam);
    
    // 홈팀이 받은 신청들 조회
    @Query("SELECT ma FROM MatchApplication ma WHERE ma.match.homeTeam = :homeTeam ORDER BY ma.createdDate DESC")
    List<MatchApplication> findReceivedApplications(@Param("homeTeam") Team homeTeam);
}