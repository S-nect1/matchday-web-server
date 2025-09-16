package com.matchday.modules.team.infrastructure;

import com.matchday.modules.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamQueryRepository {
    
    // 초대코드로 팀 조회
    Optional<Team> findByInviteCode(String inviteCode);
    
    // 초대코드 중복 확인
    boolean existsByInviteCode(String inviteCode);
    
    // 팀장 또는 매니저인 팀 조회 (TeamUser를 통해)
    @Query("SELECT t FROM Team t JOIN TeamUser tu ON t.id = tu.team.id " +
           "WHERE tu.user.id = :userId AND tu.role IN ('LEADER', 'MANAGER')")
    Optional<Team> findByUserIdWithManagementRole(@Param("userId") Long userId);
}