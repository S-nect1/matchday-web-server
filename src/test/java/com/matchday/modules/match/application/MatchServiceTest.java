package com.matchday.modules.match.application;

import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.MatchFixture;
import com.matchday.modules.match.api.dto.dto.response.MatchListResponse;
import com.matchday.modules.match.api.dto.dto.response.MatchResponse;
import com.matchday.modules.match.exception.MatchControllerAdvice;
import com.matchday.modules.match.infrastructure.MatchRepository;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.domain.TeamUser;
import com.matchday.modules.team.domain.enums.TeamRole;
import com.matchday.modules.team.exception.TeamControllerAdvice;
import com.matchday.modules.team.infrastructure.TeamRepository;
import com.matchday.modules.team.infrastructure.TeamUserRepository;
import com.matchday.modules.team.application.TeamUserService;
import com.matchday.modules.team.team.domain.TeamFixture;
import com.matchday.modules.user.domain.User;
import com.matchday.modules.user.user.domain.UserFixture;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchService 테스트")
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    
    @Mock
    private TeamRepository teamRepository;
    
    @Mock
    private TeamUserRepository teamUserRepository;
    
    @Mock
    private AddressParsingService addressParsingService;
    
    @Mock
    private TeamUserService teamUserService;
    
    @InjectMocks
    private MatchService matchService;

    @Test
    @DisplayName("매치 상세 조회 성공")
    void getMatchDetails_성공() {
        // given
        Long matchId = 1L;
        Team homeTeam = TeamFixture.defaultTeam();
        Match match = MatchFixture.matchWithTeam(homeTeam);
        ReflectionTestUtils.setField(match, "id", matchId);
        
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));
        
        // when
        MatchResponse result = matchService.getMatchDetails(matchId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getMatchId()).isEqualTo(matchId);
    }
    
    @Test
    @DisplayName("매치 상세 조회 실패 - 매치를 찾을 수 없음")
    void getMatchDetails_실패_매치없음() {
        // given
        Long matchId = 1L;
        given(matchRepository.findById(matchId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> matchService.getMatchDetails(matchId))
            .isInstanceOf(MatchControllerAdvice.class);
    }

    @Test
    @DisplayName("팀이 등록한 매치 목록 조회 성공")
    void getTeamMatches_성공() {
        // given
        Long userId = 1L;
        Long teamId = 1L;
        
        User user = UserFixture.defaultUser1();
        Team team = TeamFixture.defaultTeam();
        TeamUser teamUser = createTeamUser(team, user, TeamRole.MEMBER);
        Match match = MatchFixture.matchWithTeam(team);
        
        ReflectionTestUtils.setField(team, "id", teamId);
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(team));
        given(teamUserRepository.findByTeamIdAndUserId(teamId, userId)).willReturn(Optional.of(teamUser));
        given(matchRepository.findByHomeTeamOrderByCreatedDateDesc(team)).willReturn(List.of(match));
        
        // when
        List<MatchListResponse> result = matchService.getTeamMatches(userId, teamId);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHomeTeamName()).isEqualTo(team.getName());
    }
    
    @Test
    @DisplayName("팀이 등록한 매치 목록 조회 실패 - 팀을 찾을 수 없음")
    void getTeamMatches_실패_팀없음() {
        // given
        Long userId = 1L;
        Long teamId = 1L;
        
        given(teamRepository.findById(teamId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> matchService.getTeamMatches(userId, teamId))
            .isInstanceOf(TeamControllerAdvice.class);
    }
    
    @Test
    @DisplayName("팀이 등록한 매치 목록 조회 실패 - 팀 멤버가 아님")
    void getTeamMatches_실패_팀멤버아님() {
        // given
        Long userId = 1L;
        Long teamId = 1L;
        Team team = TeamFixture.defaultTeam();
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(team));
        given(teamUserRepository.findByTeamIdAndUserId(teamId, userId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> matchService.getTeamMatches(userId, teamId))
            .isInstanceOf(MatchControllerAdvice.class);
    }

    @Test
    @DisplayName("매치 취소 성공")
    void cancelMatch_성공() {
        // given
        Long matchId = 1L;
        Long userId = 1L;
        
        Team homeTeam = TeamFixture.defaultTeam();
        Match match = MatchFixture.matchWithTeam(homeTeam);
        
        ReflectionTestUtils.setField(homeTeam, "id", 1L);
        ReflectionTestUtils.setField(match, "id", matchId);
        
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));
        
        // when
        matchService.cancelMatch(matchId, userId);
        
        // then
        verify(teamUserService).validatePermission(homeTeam.getId(), userId);
        // match.cancelMatch() 메서드가 호출되었는지는 실제 구현에 따라 검증 방법이 달라질 수 있음
    }
    
    @Test
    @DisplayName("매치 취소 실패 - 매치를 찾을 수 없음")
    void cancelMatch_실패_매치없음() {
        // given
        Long matchId = 1L;
        Long userId = 1L;
        
        given(matchRepository.findById(matchId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> matchService.cancelMatch(matchId, userId))
            .isInstanceOf(MatchControllerAdvice.class);
    }

    private TeamUser createTeamUser(Team team, User user, TeamRole role) {
        return TeamUser.joinTeamWithBackNumber(team, user, role, 7);
    }
}