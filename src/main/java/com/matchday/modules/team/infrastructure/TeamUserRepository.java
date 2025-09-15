package com.matchday.modules.team.infrastructure;

import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.domain.TeamUser;
import com.matchday.modules.team.domain.enums.TeamRole;
import com.matchday.modules.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    
    // 특정 팀의 팀원 목록 조회
    List<TeamUser> findByTeam(Team team);
    
    // 특정 사용자의 팀 목록 조회
    List<TeamUser> findByUser(User user);

    // 팀과 사용자로 팀원 정보 조회
    Optional<TeamUser> findByTeamAndUser(Team team, User user);

    // 팀과 사용자로 팀원 정보 조회(id)
    Optional<TeamUser> findByTeamIdAndUserId(Long teamId, Long userId);

    // 특정 팀의 특정 역할 팀원 조회
    List<TeamUser> findByTeamAndRole(Team team, TeamRole role);

    // 특정 팀의 특정 역할 팀원 조회(id)
    List<TeamUser> findByTeamIdAndRole(Long teamId, TeamRole role);

    // 특정 팀의 리더 조회
    @Query("SELECT tu FROM TeamUser tu JOIN FETCH tu.user WHERE tu.team = :team AND tu.role = 'LEADER'")
    Optional<TeamUser> findLeaderByTeam(@Param("team") Team team);
    
    // 팀원 수 조회
    long countByTeam(Team team);
    
    // 팀원 수 조회 (ID로)
    Integer countByTeamId(Long teamId);
    
    // 특정 팀에서 등번호 중복 확인
    boolean existsByTeamAndBackNumber(Team team, Integer backNumber);
    
    // 사용자가 특정 팀에 가입되어 있는지 확인
    boolean existsByTeamAndUser(Team team, User user);
    
    // 사용자의 팀 가입 여부 확인 (전체 팀 대상)
    boolean existsByUser(User user);
}