package com.matchday.team.repository;

import com.matchday.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    
    // 초대코드로 팀 조회
    Optional<Team> findByInviteCode(String inviteCode);
    
    // 초대코드 중복 확인
    boolean existsByInviteCode(String inviteCode);
    
    // 팀 이름으로 검색 (LIKE 검색)
    @Query("SELECT t FROM Team t WHERE t.name LIKE %:name%")
    Optional<Team> findByNameContaining(@Param("name") String name);
}