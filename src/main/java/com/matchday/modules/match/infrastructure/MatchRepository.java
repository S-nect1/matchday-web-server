package com.matchday.modules.match.infrastructure;

import com.matchday.modules.match.domain.Match;
import com.matchday.modules.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long>, MatchQueryRepository {
    
    @Query("SELECT m.id FROM Match m WHERE m.homeTeam = :team AND m.date = :date " +
           "AND ((m.startTime <= :endTime AND m.endTime >= :startTime))")
    List<Long> findConflictingMatches(@Param("team") Team team,
                                     @Param("date") LocalDate date,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime);
    
    List<Match> findByHomeTeamOrderByCreatedAtDesc(Team homeTeam);
}