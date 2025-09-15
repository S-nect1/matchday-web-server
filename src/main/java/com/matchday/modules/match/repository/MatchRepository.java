package com.matchday.modules.match.repository;

import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.dto.projection.MatchListProjection;
import com.matchday.modules.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    
    @Query("SELECT m.id FROM Match m WHERE m.homeTeam = :team AND m.date = :date " +
           "AND ((m.startTime <= :endTime AND m.endTime >= :startTime))")
    List<Long> findConflictingMatches(@Param("team") Team team,
                                     @Param("date") LocalDate date,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime);
    
    List<Match> findByHomeTeamOrderByCreatedDateDesc(Team homeTeam);
    
    // PENDING 상태인 매치들을 조회 (신청 가능한 매치)
    @Query("SELECT m.id as id, " +
           "t.name as homeTeamName, " +
           "m.city as city, " +
           "m.district as district, " +
           "m.placeName as placeName, " +
           "m.date as date, " +
           "m.startTime as startTime, " +
           "m.endTime as endTime, " +
           "m.fee as fee, " +
           "m.matchSize as matchSize, " +
           "m.sportsType as sportsType, " +
           "m.createdDate as createdDate " +
           "FROM Match m JOIN m.homeTeam t " +
           "WHERE m.status = :status " +
           "ORDER BY m.date ASC, m.startTime ASC")
    List<MatchListProjection> findAvailableMatches(@Param("status") MatchStatus status);
}