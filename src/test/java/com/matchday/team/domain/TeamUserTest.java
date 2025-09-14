package com.matchday.team.domain;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.domain.TeamUser;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamRole;
import com.matchday.modules.team.domain.enums.TeamType;
import com.matchday.modules.team.domain.enums.Position;
import com.matchday.modules.team.exception.TeamControllerAdvice;
import com.matchday.modules.user.domain.User;
import com.matchday.modules.user.domain.enums.Gender;
import com.matchday.modules.user.domain.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class TeamUserTest {

    private Team team;
    private User user;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        
        team = Team.createTeam("Test Team", "설명", TeamType.CLUB,
                              City.SEOUL, District.SEOUL_GANGNAM, "#FF0000",
                              true, GroupGender.MALE, 20,
                              "국민은행", "123-456-789", null);

        user = User.createUser("test@example.com", "password", "테스트유저",
                              LocalDate.of(1990, 1, 1), 180, Gender.MALE,
                              Position.MF, UserRole.ROLE_MEMBER, "010-1234-5678",
                              City.SEOUL, District.SEOUL_GANGNAM, false, passwordEncoder);
    }

    @Test
    @DisplayName("팀원 가입이 올바르게 동작한다")
    void 팀원_가입_성공() {
        // when
        TeamUser teamUser = TeamUser.joinTeamWithBackNumber(team, user, TeamRole.MEMBER, 10);

        // then
        assertThat(teamUser.getTeam()).isEqualTo(team);
        assertThat(teamUser.getUser()).isEqualTo(user);
        assertThat(teamUser.getRole()).isEqualTo(TeamRole.MEMBER);
        assertThat(teamUser.getBackNumber()).isEqualTo(10);
    }

    @Test
    @DisplayName("팀장 생성이 올바르게 동작한다")
    void 팀장_생성_성공() {
        // when
        TeamUser teamLeader = TeamUser.createLeader(team, user);

        // then
        assertThat(teamLeader.getTeam()).isEqualTo(team);
        assertThat(teamLeader.getUser()).isEqualTo(user);
        assertThat(teamLeader.getRole()).isEqualTo(TeamRole.LEADER);
        assertThat(teamLeader.isLeader()).isTrue();
        assertThat(teamLeader.hasManagementAuthority()).isTrue();
    }

    @Test
    @DisplayName("권한 변경이 올바르게 동작한다")
    void 권한_변경_성공() {
        // given
        TeamUser teamUser = TeamUser.joinTeamWithBackNumber(team, user, TeamRole.MEMBER, 10);

        // when
        teamUser.changeRole(TeamRole.MANAGER);

        // then
        assertThat(teamUser.getRole()).isEqualTo(TeamRole.MANAGER);
        assertThat(teamUser.isManager()).isTrue();
        assertThat(teamUser.hasManagementAuthority()).isTrue();
    }

    @Test
    @DisplayName("등번호 변경이 올바르게 동작한다")
    void 등번호_변경_성공() {
        // given
        TeamUser teamUser = TeamUser.joinTeamWithBackNumber(team, user, TeamRole.MEMBER, 10);

        // when
        teamUser.changeBackNumber(7);

        // then
        assertThat(teamUser.getBackNumber()).isEqualTo(7);
    }

    @Test
    @DisplayName("팀 정보가 null이면 예외가 발생한다")
    void 팀원_가입_실패_팀_null() {
        // when & then
        assertThatThrownBy(() -> TeamUser.joinTeamWithBackNumber(null, user, TeamRole.MEMBER, 10))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀 정보는 필수입니다.");
    }

    @Test
    @DisplayName("사용자 정보가 null이면 예외가 발생한다")
    void 팀원_가입_실패_사용자_null() {
        // when & then
        assertThatThrownBy(() -> TeamUser.joinTeamWithBackNumber(team, null, TeamRole.MEMBER, 10))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("사용자 정보는 필수입니다.");
    }

    @Test
    @DisplayName("역할이 null이면 예외가 발생한다")
    void 팀원_가입_실패_역할_null() {
        // when & then
        assertThatThrownBy(() -> TeamUser.joinTeamWithBackNumber(team, user, null, 10))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀 역할은 필수입니다.");
    }

    @Test
    @DisplayName("권한 확인 메서드들이 올바르게 동작한다")
    void 권한_확인_메서드() {
        // given
        TeamUser leader = TeamUser.joinTeamWithBackNumber(team, user, TeamRole.LEADER, null);
        TeamUser manager = TeamUser.joinTeamWithBackNumber(team, user, TeamRole.MANAGER, null);
        TeamUser member = TeamUser.joinTeamWithBackNumber(team, user, TeamRole.MEMBER, null);

        // when & then
        assertThat(leader.isLeader()).isTrue();
        assertThat(leader.isManager()).isFalse();
        assertThat(leader.isMember()).isFalse();
        assertThat(leader.hasManagementAuthority()).isTrue();

        assertThat(manager.isLeader()).isFalse();
        assertThat(manager.isManager()).isTrue();
        assertThat(manager.isMember()).isFalse();
        assertThat(manager.hasManagementAuthority()).isTrue();

        assertThat(member.isLeader()).isFalse();
        assertThat(member.isManager()).isFalse();
        assertThat(member.isMember()).isTrue();
        assertThat(member.hasManagementAuthority()).isFalse();
    }
}