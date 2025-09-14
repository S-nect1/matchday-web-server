package com.matchday.team.domain;

import com.matchday.global.entity.BaseEntity;
import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.team.domain.enums.TeamRole;
import com.matchday.team.exception.advice.TeamControllerAdvice;
import com.matchday.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "team_user", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"team_id", "user_id"}),
           @UniqueConstraint(columnNames = {"team_id", "back_number"})
       })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    // FIXME: 기획에 따라서 수정될 수 있음
    @Column(name = "back_number")
    private Integer backNumber;

    // 팀원 가입 정적 팩토리 메서드 (등번호 입력) - 테스트에서 사용
    public static TeamUser joinTeamWithBackNumber(Team team, User user, TeamRole role, Integer backNumber) {
        validateJoinTeam(team, user, role);
        
        TeamUser teamUser = new TeamUser();
        teamUser.team = team;
        teamUser.user = user;
        teamUser.role = role;
        teamUser.backNumber = backNumber;
        
        return teamUser;
    }

    public static TeamUser joinTeam(Team team, User user, TeamRole role) {
        validateJoinTeam(team, user, role);

        TeamUser teamUser = new TeamUser();
        teamUser.team = team;
        teamUser.user = user;
        teamUser.role = role;
        teamUser.backNumber = user.getBackNumber();

        return teamUser;
    }

    // 팀장으로 팀 생성 시 사용
    public static TeamUser createLeader(Team team, User user) {
        return joinTeam(team, user, TeamRole.LEADER);
    }

    // 권한 변경
    public void changeRole(TeamRole newRole) {
        if (newRole == null) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_ROLE_REQUIRED);
        }
        this.role = newRole;
    }

    // 등번호 변경
    public void changeBackNumber(Integer backNumber) {
        this.backNumber = backNumber;
    }

    // 권한 확인 메서드들
    public boolean isLeader() {
        return this.role == TeamRole.LEADER;
    }

    public boolean isManager() {
        return this.role == TeamRole.MANAGER;
    }

    public boolean isMember() {
        return this.role == TeamRole.MEMBER;
    }

    public boolean hasManagementAuthority() {
        return isLeader() || isManager();
    }

    // 검증 메서드
    private static void validateJoinTeam(Team team, User user, TeamRole role) {
        if (team == null) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_INFO_REQUIRED);
        }
        if (user == null) {
            throw new TeamControllerAdvice(ResponseCode.USER_INFO_REQUIRED);
        }
        if (role == null) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_ROLE_REQUIRED);
        }
    }
}
