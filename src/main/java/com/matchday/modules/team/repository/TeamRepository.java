package com.matchday.modules.team.repository;

import com.matchday.modules.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamQueryRepository {
    
    // 초대코드로 팀 조회
    Optional<Team> findByInviteCode(String inviteCode);
    
    // 초대코드 중복 확인
    boolean existsByInviteCode(String inviteCode);
}