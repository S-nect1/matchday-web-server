package com.matchday.team.service;

import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.team.domain.Team;
import com.matchday.team.domain.TeamFixture;
import com.matchday.team.domain.TeamUser;
import com.matchday.team.domain.enums.TeamRole;
import com.matchday.team.dto.request.TeamJoinRequest;
import com.matchday.team.dto.response.TeamResponse;
import com.matchday.team.dto.response.TeamUserResponse;
import com.matchday.team.exception.advice.TeamControllerAdvice;
import com.matchday.team.repository.TeamRepository;
import com.matchday.team.repository.TeamUserRepository;
import com.matchday.user.domain.User;
import com.matchday.user.domain.UserFixture;
import com.matchday.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamUserService 테스트")
class TeamUserServiceTest {

    @Mock
    private TeamRepository teamRepository;
    
    @Mock
    private TeamUserRepository teamUserRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private TeamUserService teamUserService;

    @Test
    @DisplayName("팀 가입 성공")
    void joinTeam_성공() {
        // given
        String inviteCode = "123456";
        TeamJoinRequest request = new TeamJoinRequest(inviteCode);
        
        User user = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser savedTeamUser = createTeamUser(team, user, TeamRole.MEMBER);
        
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamRepository.findByInviteCode(inviteCode)).willReturn(Optional.of(team));
        given(teamUserRepository.existsByTeamAndUser(team, user)).willReturn(false);
        given(teamUserRepository.countByTeam(team)).willReturn(5L);
//        given(teamUserRepository.existsByTeamAndBackNumber(team, 7)).willReturn(false);
        given(teamUserRepository.save(any(TeamUser.class))).willReturn(savedTeamUser);
        
        // when
        TeamUserResponse result = teamUserService.joinTeam(user.getId(), request);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(TeamRole.MEMBER);
//        assertThat(result.backNumber()).isEqualTo(7);
        assertThat(result.user().id()).isEqualTo(user.getId());
        
        verify(teamUserRepository).save(any(TeamUser.class));
    }

    @Test
    @DisplayName("팀 가입 실패 - 사용자를 찾을 수 없음")
    void joinTeam_실패_사용자없음() {
        // given
        Long userId = 1L;
        TeamJoinRequest request = new TeamJoinRequest("123456");
        
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> teamUserService.joinTeam(userId, request))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("올바르지 않은 사용자입니다.");
    }

    @Test
    @DisplayName("팀 가입 실패 - 팀을 찾을 수 없음")
    void joinTeam_실패_팀없음() {
        // given
        String inviteCode = "123456";
        TeamJoinRequest request = new TeamJoinRequest(inviteCode);
        User user = UserFixture.defaultUser1();
        
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamRepository.findByInviteCode(inviteCode)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> teamUserService.joinTeam(user.getId(), request))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("팀 가입 실패 - 이미 가입된 팀")
    void joinTeam_실패_이미가입() {
        // given
        String inviteCode = "123456";
        TeamJoinRequest request = new TeamJoinRequest(inviteCode);
        User user = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamRepository.findByInviteCode(inviteCode)).willReturn(Optional.of(team));
        given(teamUserRepository.existsByTeamAndUser(team, user)).willReturn(true);
        
        // when & then
        assertThatThrownBy(() -> teamUserService.joinTeam(user.getId(), request))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("이미 팀에 가입되어 있습니다.");
    }

    @Test
    @DisplayName("팀 가입 실패 - 멤버 수 제한 초과")
    void joinTeam_실패_멤버수제한() {
        // given
        String inviteCode = "123456";
        TeamJoinRequest request = new TeamJoinRequest(inviteCode);
        User user = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamRepository.findByInviteCode(inviteCode)).willReturn(Optional.of(team));
        given(teamUserRepository.existsByTeamAndUser(team, user)).willReturn(false);
        given(teamUserRepository.countByTeam(team)).willReturn(20L); // 이미 20명 가입
        
        // when & then
        assertThatThrownBy(() -> teamUserService.joinTeam(user.getId(), request))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀 인원이 가득 찼습니다.");
    }

//    @Test
//    @DisplayName("팀 가입 실패 - 등번호 중복")
//    void joinTeam_실패_등번호중복() {
//        // given
//        Long userId = 1L;
//        String inviteCode = "123456";
//        TeamJoinRequest request = new TeamJoinRequest(inviteCode);
//        User user = createUser(userId, "test@test.com", "테스트유저", 7);
//        Team team = createTeam("테스트팀", inviteCode, 10);
//
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        given(teamRepository.findByInviteCode(inviteCode)).willReturn(Optional.of(team));
//        given(teamUserRepository.existsByTeamAndUser(team, user)).willReturn(false);
//        given(teamUserRepository.countByTeam(team)).willReturn(3L);
//        given(teamUserRepository.existsByTeamAndBackNumber(team, 7)).willReturn(true);
//
//        // when & then
//        assertThatThrownBy(() -> teamUserService.joinTeam(userId, request))
//            .isInstanceOf(TeamControllerAdvice.class)
//            .hasFieldOrPropertyWithValue("responseCode", ResponseCode.BACK_NUMBER_ALREADY_EXIST);
//    }

    @Test
    @DisplayName("팀 탈퇴 성공")
    void leaveTeam_성공() {
        // given
        User user = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser teamUser = createTeamUser(team, user, TeamRole.MEMBER);
        
        given(teamUserRepository.findByTeamIdAndUserId(team.getId(), user.getId()))
            .willReturn(Optional.of(teamUser));
        
        // when
        teamUserService.leaveTeam(user.getId(), team.getId());
        
        // then
        verify(teamUserRepository).delete(teamUser);
    }

    @Test
    @DisplayName("팀 탈퇴 실패 - 팀 멤버를 찾을 수 없음")
    void leaveTeam_실패_멤버없음() {
        // given
        Long userId = 1L;
        Long teamId = 1L;
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, userId))
            .willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> teamUserService.leaveTeam(userId, teamId))
            .isInstanceOf(TeamControllerAdvice.class)
                .hasMessage("팀 멤버를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("팀 탈퇴 실패 - 팀장은 탈퇴 불가")
    void leaveTeam_실패_팀장탈퇴불가() {
        // given
        User user = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser leaderTeamUser = createTeamUser(team, user, TeamRole.LEADER);
        
        given(teamUserRepository.findByTeamIdAndUserId(team.getId(), user.getId()))
            .willReturn(Optional.of(leaderTeamUser));
        
        // when & then
        assertThatThrownBy(() -> teamUserService.leaveTeam(user.getId(), team.getId()))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀장은 팀을 탈퇴할 수 없습니다.");
    }

    @Test
    @DisplayName("멤버 수 조회")
    void getMemberCount_성공() {
        // given
        Long teamId = 1L;
        given(teamUserRepository.countByTeamId(teamId)).willReturn(5);
        
        // when
        Integer result = teamUserService.getMemberCount(teamId);
        
        // then
        assertThat(result).isEqualTo(5);
    }

    @Test
    @DisplayName("팀 멤버 목록 조회 성공")
    void getTeamMembers_성공() {
        // given
        Long teamId = 1L;
        Team team = TeamFixture.defaultTeam();
        User user1 = UserFixture.defaultUser1();
        User user2 = UserFixture.defaultUser2();
        TeamUser teamUser1 = createTeamUser(team, user1, TeamRole.LEADER);
        TeamUser teamUser2 = createTeamUser(team, user2, TeamRole.MEMBER);
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(team));
        given(teamUserRepository.findByTeam(team)).willReturn(List.of(teamUser1, teamUser2));
        
        // when
        List<TeamUserResponse> result = teamUserService.getTeamMembers(teamId);
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).role()).isEqualTo(TeamRole.LEADER);
        assertThat(result.get(1).role()).isEqualTo(TeamRole.MEMBER);
    }

    @Test
    @DisplayName("팀 멤버 목록 조회 실패 - 팀을 찾을 수 없음")
    void getTeamMembers_실패_팀없음() {
        // given
        Long teamId = 1L;
        given(teamRepository.findById(teamId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> teamUserService.getTeamMembers(teamId))
            .isInstanceOf(TeamControllerAdvice.class)
                .hasMessage("팀을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자가 가입한 팀 목록 조회 성공")
    void getUserTeams_성공() {
        // given
        User user = UserFixture.defaultUser1();
        Team team1 = TeamFixture.teamWithName("팀1");
        Team team2 = TeamFixture.teamWithName("팀2");
        TeamUser teamUser1 = createTeamUser(team1, user, TeamRole.MEMBER);
        TeamUser teamUser2 = createTeamUser(team2, user, TeamRole.LEADER);
        ReflectionTestUtils.setField(team1, "id", 1L);
        ReflectionTestUtils.setField(team2, "id", 2L);
        
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamUserRepository.findByUser(user)).willReturn(List.of(teamUser1, teamUser2));
        given(teamUserRepository.countByTeamId(team1.getId())).willReturn(5);
        given(teamUserRepository.countByTeamId(team2.getId())).willReturn(8);
        
        // when
        List<TeamResponse> result = teamUserService.getUserTeams(user.getId());
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(TeamResponse::name, TeamResponse::memberCount)
                .containsExactlyInAnyOrder(
                        tuple("팀1", 5),
                        tuple("팀2", 8)
                );
    }

    @Test
    @DisplayName("사용자가 가입한 팀 목록 조회 실패 - 사용자를 찾을 수 없음")
    void getUserTeams_실패_사용자없음() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> teamUserService.getUserTeams(userId))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("올바르지 않은 사용자입니다.");
    }

    @Test
    @DisplayName("멤버 역할 변경 성공")
    void updateMemberRole_성공() {
        // given
        Long teamId = 1L;
        Long targetUserId = 2L;
        Long requestUserId = 1L;
        TeamRole newRole = TeamRole.MANAGER;
        
        User requestUser = UserFixture.defaultUser1();
        User targetUser = UserFixture.defaultUser2();
        Team team = TeamFixture.defaultTeam();
        TeamUser requestTeamUser = createTeamUser(team, requestUser, TeamRole.LEADER);
        TeamUser targetTeamUser = createTeamUser(team, targetUser, TeamRole.MEMBER);
        TeamUser updatedTeamUser = createTeamUser(team, targetUser, TeamRole.MANAGER);
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, requestUserId))
            .willReturn(Optional.of(requestTeamUser));
        given(teamUserRepository.findByTeamIdAndUserId(teamId, targetUserId))
            .willReturn(Optional.of(targetTeamUser));
        given(teamUserRepository.save(targetTeamUser)).willReturn(updatedTeamUser);
        
        // when
        TeamUserResponse result = teamUserService.updateMemberRole(teamId, targetUserId, newRole, requestUserId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(TeamRole.MANAGER);
        // Note: targetTeamUser is not a mock, so we can't verify method calls on it
        verify(teamUserRepository).save(targetTeamUser);
    }
    
    @Test
    @DisplayName("팀장 권한 위임 성공")
    void updateMemberRole_팀장위임_성공() {
        // given
        Long teamId = 1L;
        Long targetUserId = 2L;
        Long requestUserId = 1L;
        TeamRole newRole = TeamRole.LEADER;
        
        User requestUser = UserFixture.defaultUser1();
        User targetUser = UserFixture.defaultUser2();
        Team team = TeamFixture.defaultTeam();
        TeamUser requestTeamUser = createTeamUser(team, requestUser, TeamRole.LEADER);
        TeamUser targetTeamUser = createTeamUser(team, targetUser, TeamRole.MEMBER);
        TeamUser updatedTargetTeamUser = createTeamUser(team, targetUser, TeamRole.LEADER);
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, requestUserId))
            .willReturn(Optional.of(requestTeamUser));
        given(teamUserRepository.findByTeamIdAndUserId(teamId, targetUserId))
            .willReturn(Optional.of(targetTeamUser));
        given(teamUserRepository.save(requestTeamUser)).willReturn(requestTeamUser);
        given(teamUserRepository.save(targetTeamUser)).willReturn(updatedTargetTeamUser);
        
        // when
        TeamUserResponse result = teamUserService.updateMemberRole(teamId, targetUserId, newRole, requestUserId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(TeamRole.LEADER);
        
        // 기존 팀장이 일반 멤버로 강등되었는지 확인
        verify(teamUserRepository, times(2)).save(any(TeamUser.class));
        verify(teamUserRepository).save(requestTeamUser); // 기존 팀장 강등
        verify(teamUserRepository).save(targetTeamUser); // 새 팀장 승급
    }

    @Test
    @DisplayName("멤버 역할 변경 실패 - 요청자를 찾을 수 없음")
    void updateMemberRole_실패_요청자없음() {
        // given
        Long teamId = 1L;
        Long targetUserId = 2L;
        Long requestUserId = 1L;
        TeamRole newRole = TeamRole.MANAGER;
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, requestUserId))
            .willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> teamUserService.updateMemberRole(teamId, targetUserId, newRole, requestUserId))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀 멤버를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("멤버 역할 변경 실패 - 권한 없음")
    void updateMemberRole_실패_권한없음() {
        // given
        Long teamId = 1L;
        Long targetUserId = 2L;
        Long requestUserId = 3L;
        TeamRole newRole = TeamRole.MANAGER;
        
        User requestUser = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser requestTeamUser = createTeamUser(team, requestUser, TeamRole.MEMBER); // 일반 멤버
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, requestUserId))
            .willReturn(Optional.of(requestTeamUser));
        
        // when & then
        assertThatThrownBy(() -> teamUserService.updateMemberRole(teamId, targetUserId, newRole, requestUserId))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("권한이 잘못되었습니다.");
    }

    @Test
    @DisplayName("멤버 역할 변경 실패 - 대상 멤버를 찾을 수 없음")
    void updateMemberRole_실패_대상멤버없음() {
        // given
        Long teamId = 1L;
        Long targetUserId = 2L;
        Long requestUserId = 1L;
        TeamRole newRole = TeamRole.MANAGER;
        
        User requestUser = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser requestTeamUser = createTeamUser(team, requestUser, TeamRole.LEADER);
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, requestUserId))
            .willReturn(Optional.of(requestTeamUser));
        given(teamUserRepository.findByTeamIdAndUserId(teamId, targetUserId))
            .willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> teamUserService.updateMemberRole(teamId, targetUserId, newRole, requestUserId))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀 멤버를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("멤버 강퇴 성공")
    void kickMember_성공() {
        // given
        Long requestUserId = 1L;
        Long targetUserId = 2L;
        Long teamId = 1L;
        
        User requestUser = UserFixture.defaultUser1();
        User targetUser = UserFixture.defaultUser2();
        Team team = TeamFixture.defaultTeam();
        TeamUser requestTeamUser = createTeamUser(team, requestUser, TeamRole.LEADER);
        TeamUser targetTeamUser = createTeamUser(team, targetUser, TeamRole.MEMBER);
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, requestUserId))
            .willReturn(Optional.of(requestTeamUser));
        given(teamUserRepository.findByTeamIdAndUserId(teamId, targetUserId))
            .willReturn(Optional.of(targetTeamUser));
        
        // when
        teamUserService.kickMember(requestUserId, targetUserId, teamId);
        
        // then
        verify(teamUserRepository).delete(targetTeamUser);
    }

    @Test
    @DisplayName("멤버 강퇴 실패 - 권한 없음")
    void kickMember_실패_권한없음() {
        // given
        Long requestUserId = 1L;
        Long targetUserId = 2L;
        Long teamId = 1L;
        
        User requestUser = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser requestTeamUser = createTeamUser(team, requestUser, TeamRole.MEMBER); // 일반 멤버
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, requestUserId))
            .willReturn(Optional.of(requestTeamUser));
        
        // when & then
        assertThatThrownBy(() -> teamUserService.kickMember(requestUserId, targetUserId, teamId))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("권한이 잘못되었습니다.");
    }

    @Test
    @DisplayName("멤버 강퇴 실패 - 대상 멤버를 찾을 수 없음")
    void kickMember_실패_대상멤버없음() {
        // given
        Long requestUserId = 1L;
        Long targetUserId = 2L;
        Long teamId = 1L;
        
        User requestUser = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser requestTeamUser = createTeamUser(team, requestUser, TeamRole.LEADER);
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, requestUserId))
            .willReturn(Optional.of(requestTeamUser));
        given(teamUserRepository.findByTeamIdAndUserId(teamId, targetUserId))
            .willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> teamUserService.kickMember(requestUserId, targetUserId, teamId))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀 멤버를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("권한 검증 성공 - 팀장")
    void validatePermission_성공_팀장() {
        // given
        Long teamId = 1L;
        Long userId = 1L;
        
        User user = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser teamUser = createTeamUser(team, user, TeamRole.LEADER);
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, userId))
            .willReturn(Optional.of(teamUser));
        
        // when & then
        assertThatCode(() -> teamUserService.validatePermission(teamId, userId))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("권한 검증 성공 - 매니저")
    void validatePermission_성공_매니저() {
        // given
        Long teamId = 1L;
        Long userId = 1L;
        
        User user = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser teamUser = createTeamUser(team, user, TeamRole.MANAGER);
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, userId))
            .willReturn(Optional.of(teamUser));
        
        // when & then
        assertThatCode(() -> teamUserService.validatePermission(teamId, userId))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("권한 검증 실패 - 일반 멤버")
    void validatePermission_실패_일반멤버() {
        // given
        Long teamId = 1L;
        Long userId = 1L;
        
        User user = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser teamUser = createTeamUser(team, user, TeamRole.MEMBER);
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, userId))
            .willReturn(Optional.of(teamUser));
        
        // when & then
        assertThatThrownBy(() -> teamUserService.validatePermission(teamId, userId))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("권한이 잘못되었습니다.");
    }

    @Test
    @DisplayName("권한 검증 실패 - 팀 멤버를 찾을 수 없음")
    void validatePermission_실패_멤버없음() {
        // given
        Long teamId = 1L;
        Long userId = 1L;
        
        given(teamUserRepository.findByTeamIdAndUserId(teamId, userId))
            .willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> teamUserService.validatePermission(teamId, userId))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀 멤버를 찾을 수 없습니다.");
    }

    private TeamUser createTeamUser(Team team, User user, TeamRole role) {
        return TeamUser.joinTeamWithBackNumber(team, user, role, 7);
    }
}