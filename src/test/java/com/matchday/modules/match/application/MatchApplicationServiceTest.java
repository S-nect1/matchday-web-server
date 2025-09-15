package com.matchday.modules.match.application;

import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.MatchApplication;
import com.matchday.modules.match.domain.enums.MatchApplicationStatus;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.api.dto.dto.request.MatchApplicationRequest;
import com.matchday.modules.match.api.dto.dto.response.MatchApplicationResponse;
import com.matchday.modules.match.exception.MatchControllerAdvice;
import com.matchday.modules.match.infrastructure.MatchApplicationRepository;
import com.matchday.modules.match.infrastructure.MatchRepository;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.exception.TeamControllerAdvice;
import com.matchday.modules.team.infrastructure.TeamRepository;
import com.matchday.modules.team.application.TeamUserService;
import com.matchday.modules.team.team.domain.TeamFixture;
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
@DisplayName("MatchApplicationService 테스트")
class MatchApplicationServiceTest {

    @Mock
    private MatchApplicationRepository matchApplicationRepository;
    
    @Mock
    private MatchRepository matchRepository;
    
    @Mock
    private TeamRepository teamRepository;
    
    @Mock
    private TeamUserService teamUserService;
    
    @InjectMocks
    private MatchApplicationService matchApplicationService;

    @Test
    @DisplayName("매치 신청 성공")
    void applyToMatch_성공() {
        // given
        Long userId = 1L;
        Long matchId = 1L;
        Long teamId = 2L;
        String message = "신청 메시지";
        MatchApplicationRequest request = createMatchApplicationRequest(teamId, message);
        
        Team homeTeam = TeamFixture.defaultTeam();
        Team applicantTeam = TeamFixture.teamWithName("신청팀");
        Match match = createValidMatch(homeTeam);
        MatchApplication savedApplication = createMatchApplication(match, applicantTeam, message);
        
        ReflectionTestUtils.setField(homeTeam, "id", 1L);
        ReflectionTestUtils.setField(applicantTeam, "id", teamId);
        ReflectionTestUtils.setField(match, "id", matchId);
        ReflectionTestUtils.setField(savedApplication, "id", 1L);
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(applicantTeam));
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));
        given(matchApplicationRepository.existsByMatchAndApplicantTeam(match, applicantTeam)).willReturn(false);
        given(matchApplicationRepository.save(any(MatchApplication.class))).willReturn(savedApplication);
        
        // when
        Long result = matchApplicationService.applyToMatch(userId, matchId, request);
        
        // then
        assertThat(result).isEqualTo(1L);
        verify(teamUserService).validatePermission(teamId, userId);
        verify(matchApplicationRepository).save(any(MatchApplication.class));
    }
    
    @Test
    @DisplayName("매치 신청 실패 - 팀을 찾을 수 없음")
    void applyToMatch_실패_팀없음() {
        // given
        Long userId = 1L;
        Long matchId = 1L;
        Long teamId = 2L;
        MatchApplicationRequest request = createMatchApplicationRequest(teamId, "메시지");
        
        given(teamRepository.findById(teamId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> matchApplicationService.applyToMatch(userId, matchId, request))
            .isInstanceOf(TeamControllerAdvice.class);
    }
    
    @Test
    @DisplayName("매치 신청 실패 - 매치를 찾을 수 없음")
    void applyToMatch_실패_매치없음() {
        // given
        Long userId = 1L;
        Long matchId = 1L;
        Long teamId = 2L;
        MatchApplicationRequest request = createMatchApplicationRequest(teamId, "메시지");
        Team applicantTeam = TeamFixture.defaultTeam();
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(applicantTeam));
        given(matchRepository.findById(matchId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> matchApplicationService.applyToMatch(userId, matchId, request))
            .isInstanceOf(MatchControllerAdvice.class);
    }
    
    @Test
    @DisplayName("매치 신청 실패 - 자신의 매치에 신청")
    void applyToMatch_실패_자신의매치() {
        // given
        Long userId = 1L;
        Long matchId = 1L;
        Long teamId = 1L;
        MatchApplicationRequest request = createMatchApplicationRequest(teamId, "메시지");
        
        Team homeTeam = TeamFixture.defaultTeam();
        Match match = mock(Match.class);
        
        ReflectionTestUtils.setField(homeTeam, "id", teamId);
        when(match.getHomeTeam()).thenReturn(homeTeam);
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(homeTeam));
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));
        
        // when & then
        assertThatThrownBy(() -> matchApplicationService.applyToMatch(userId, matchId, request))
            .isInstanceOf(MatchControllerAdvice.class);
    }
    
    @Test
    @DisplayName("매치 신청 실패 - 중복 신청")
    void applyToMatch_실패_중복신청() {
        // given
        Long userId = 1L;
        Long matchId = 1L;
        Long teamId = 2L;
        MatchApplicationRequest request = createMatchApplicationRequest(teamId, "메시지");
        
        Team homeTeam = TeamFixture.defaultTeam();
        Team applicantTeam = TeamFixture.teamWithName("신청팀");
        Match match = createValidMatch(homeTeam);
        
        ReflectionTestUtils.setField(homeTeam, "id", 1L);
        ReflectionTestUtils.setField(applicantTeam, "id", teamId);
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(applicantTeam));
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));
        given(matchApplicationRepository.existsByMatchAndApplicantTeam(match, applicantTeam)).willReturn(true);
        
        // when & then
        assertThatThrownBy(() -> matchApplicationService.applyToMatch(userId, matchId, request))
            .isInstanceOf(MatchControllerAdvice.class);
    }
    
    @Test
    @DisplayName("매치 신청 목록 조회 성공")
    void getMatchApplications_성공() {
        // given
        Long userId = 1L;
        Long matchId = 1L;
        
        Team homeTeam = TeamFixture.defaultTeam();
        Team applicantTeam = TeamFixture.teamWithName("신청팀");
        Match match = mock(Match.class);
        MatchApplication application = createMatchApplication(match, applicantTeam, "신청 메시지");
        
        ReflectionTestUtils.setField(homeTeam, "id", 1L);
        when(match.getHomeTeam()).thenReturn(homeTeam);
        
        given(matchRepository.findById(matchId)).willReturn(Optional.of(match));
        given(matchApplicationRepository.findByMatchOrderByCreatedDateDesc(match))
            .willReturn(List.of(application));
        
        // when
        List<MatchApplicationResponse> result = matchApplicationService.getMatchApplications(userId, matchId);
        
        // then
        assertThat(result).hasSize(1);
        verify(teamUserService).validatePermission(homeTeam.getId(), userId);
    }
    
    @Test
    @DisplayName("팀이 신청한 목록 조회 성공")
    void getTeamApplications_성공() {
        // given
        Long userId = 1L;
        Long teamId = 1L;
        
        Team applicantTeam = TeamFixture.defaultTeam();
        Team homeTeam = TeamFixture.teamWithName("홈팀");
        Match match = mock(Match.class);
        MatchApplication application = createMatchApplication(match, applicantTeam, "신청 메시지");
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(applicantTeam));
        given(matchApplicationRepository.findByApplicantTeamOrderByCreatedDateDesc(applicantTeam))
            .willReturn(List.of(application));
        
        // when
        List<MatchApplicationResponse> result = matchApplicationService.getTeamApplications(userId, teamId);
        
        // then
        assertThat(result).hasSize(1);
        verify(teamUserService).validatePermission(teamId, userId);
    }
    
    @Test
    @DisplayName("팀이 받은 신청 목록 조회 성공")
    void getReceivedApplications_성공() {
        // given
        Long userId = 1L;
        Long teamId = 1L;
        
        Team homeTeam = TeamFixture.defaultTeam();
        Team applicantTeam = TeamFixture.teamWithName("신청팀");
        Match match = mock(Match.class);
        MatchApplication application = createMatchApplication(match, applicantTeam, "신청 메시지");
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(homeTeam));
        given(matchApplicationRepository.findReceivedApplications(homeTeam))
            .willReturn(List.of(application));
        
        // when
        List<MatchApplicationResponse> result = matchApplicationService.getReceivedApplications(userId, teamId);
        
        // then
        assertThat(result).hasSize(1);
        verify(teamUserService).validatePermission(teamId, userId);
    }
    
    @Test
    @DisplayName("매치 신청 수락 성공")
    void acceptApplication_성공() {
        // given
        Long userId = 1L;
        Long applicationId = 1L;
        
        Team homeTeam = TeamFixture.defaultTeam();
        Team applicantTeam = TeamFixture.teamWithName("신청팀");
        Match match = createPendingMatch(homeTeam);
        MatchApplication application = createMatchApplication(match, applicantTeam, "신청 메시지");
        
        ReflectionTestUtils.setField(homeTeam, "id", 1L);
        ReflectionTestUtils.setField(application, "id", applicationId);
        
        given(matchApplicationRepository.findById(applicationId)).willReturn(Optional.of(application));
        given(matchApplicationRepository.findByMatchAndStatus(match, MatchApplicationStatus.APPLIED))
            .willReturn(List.of(application));
        
        // when
        matchApplicationService.acceptApplication(userId, applicationId);
        
        // then
        verify(teamUserService).validatePermission(homeTeam.getId(), userId);
        assertThat(application.getStatus()).isEqualTo(MatchApplicationStatus.ACCEPTED);
        assertThat(match.getStatus()).isEqualTo(MatchStatus.CONFIRMED);
        verify(matchApplicationRepository).findByMatchAndStatus(match, MatchApplicationStatus.APPLIED);
    }

    @Test
    @DisplayName("매치 신청 수락 성공 - 다른 신청들 자동 거절")
    void acceptApplication_성공_다른신청들자동거절() {
        // given
        Long userId = 1L;
        Long acceptedApplicationId = 1L;
        
        Team homeTeam = TeamFixture.defaultTeam();
        Team applicantTeam1 = TeamFixture.teamWithName("신청팀1");
        Team applicantTeam2 = TeamFixture.teamWithName("신청팀2");
        Team applicantTeam3 = TeamFixture.teamWithName("신청팀3");
        
        Match match = createPendingMatch(homeTeam);
        MatchApplication acceptedApplication = createMatchApplication(match, applicantTeam1, "수락될 신청");
        MatchApplication rejectedApplication1 = createMatchApplication(match, applicantTeam2, "거절될 신청1");
        MatchApplication rejectedApplication2 = createMatchApplication(match, applicantTeam3, "거절될 신청2");
        
        ReflectionTestUtils.setField(homeTeam, "id", 1L);
        ReflectionTestUtils.setField(acceptedApplication, "id", acceptedApplicationId);
        ReflectionTestUtils.setField(rejectedApplication1, "id", 2L);
        ReflectionTestUtils.setField(rejectedApplication2, "id", 3L);
        
        List<MatchApplication> allApplications = List.of(
            acceptedApplication, rejectedApplication1, rejectedApplication2
        );
        
        given(matchApplicationRepository.findById(acceptedApplicationId))
            .willReturn(Optional.of(acceptedApplication));
        given(matchApplicationRepository.findByMatchAndStatus(match, MatchApplicationStatus.APPLIED))
            .willReturn(allApplications);
        
        // when
        matchApplicationService.acceptApplication(userId, acceptedApplicationId);
        
        // then
        verify(teamUserService).validatePermission(homeTeam.getId(), userId);
        
        // 수락된 신청 확인
        assertThat(acceptedApplication.getStatus()).isEqualTo(MatchApplicationStatus.ACCEPTED);
        assertThat(match.getStatus()).isEqualTo(MatchStatus.CONFIRMED);
        
        // 다른 신청들이 거절되었는지 확인
        assertThat(rejectedApplication1.getStatus()).isEqualTo(MatchApplicationStatus.REJECTED);
        assertThat(rejectedApplication2.getStatus()).isEqualTo(MatchApplicationStatus.REJECTED);
        
        // rejectOtherApplications 메서드가 호출되었는지 확인
        verify(matchApplicationRepository).findByMatchAndStatus(match, MatchApplicationStatus.APPLIED);
    }
    
    @Test
    @DisplayName("매치 신청 수락 실패 - 신청을 찾을 수 없음")
    void acceptApplication_실패_신청없음() {
        // given
        Long userId = 1L;
        Long applicationId = 1L;
        
        given(matchApplicationRepository.findById(applicationId)).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> matchApplicationService.acceptApplication(userId, applicationId))
            .isInstanceOf(MatchControllerAdvice.class);
    }
    
    @Test
    @DisplayName("매치 신청 거절 성공")
    void rejectApplication_성공() {
        // given
        Long userId = 1L;
        Long applicationId = 1L;
        
        Team homeTeam = TeamFixture.defaultTeam();
        Team applicantTeam = TeamFixture.teamWithName("신청팀");
        Match match = mock(Match.class);
        MatchApplication application = createMatchApplication(match, applicantTeam, "신청 메시지");
        
        ReflectionTestUtils.setField(homeTeam, "id", 1L);
        when(match.getHomeTeam()).thenReturn(homeTeam);
        
        given(matchApplicationRepository.findById(applicationId)).willReturn(Optional.of(application));
        
        // when
        matchApplicationService.rejectApplication(userId, applicationId);
        
        // then
        verify(teamUserService).validatePermission(homeTeam.getId(), userId);
        assertThat(application.getStatus()).isEqualTo(MatchApplicationStatus.REJECTED);
    }
    
    @Test
    @DisplayName("매치 신청 취소 성공")
    void cancelApplication_성공() {
        // given
        Long userId = 1L;
        Long applicationId = 1L;
        
        Team homeTeam = TeamFixture.defaultTeam();
        Team applicantTeam = TeamFixture.teamWithName("신청팀");
        Match match = mock(Match.class);
        MatchApplication application = createMatchApplication(match, applicantTeam, "신청 메시지");
        
        ReflectionTestUtils.setField(applicantTeam, "id", 1L);
        
        given(matchApplicationRepository.findById(applicationId)).willReturn(Optional.of(application));
        
        // when
        matchApplicationService.cancelApplication(userId, applicationId);
        
        // then
        verify(teamUserService).validatePermission(applicantTeam.getId(), userId);
        assertThat(application.getStatus()).isEqualTo(MatchApplicationStatus.CANCELED);
    }

    private MatchApplication createMatchApplication(Match match, Team applicantTeam, String message) {
        return MatchApplication.createApplication(match, applicantTeam, message);
    }
    
    private Match createPendingMatch(Team homeTeam) {
        Match match = mock(Match.class);
        when(match.getHomeTeam()).thenReturn(homeTeam);
        when(match.getStatus()).thenReturn(MatchStatus.PENDING);
        // acceptMatch() 호출 시 상태 변경을 시뮬레이션
        doAnswer(invocation -> {
            when(match.getStatus()).thenReturn(MatchStatus.CONFIRMED);
            return null;
        }).when(match).acceptMatch();
        return match;
    }
    
    private MatchApplicationRequest createMatchApplicationRequest(Long teamId, String message) {
        MatchApplicationRequest request = new MatchApplicationRequest();
        ReflectionTestUtils.setField(request, "teamId", teamId);
        ReflectionTestUtils.setField(request, "message", message);
        return request;
    }
    
    private Match createValidMatch(Team homeTeam) {
        Match match = mock(Match.class);
        when(match.getHomeTeam()).thenReturn(homeTeam);
        when(match.getStatus()).thenReturn(MatchStatus.PENDING); // 이제 PENDING이 올바른 상태
        when(match.isAvailableForApplication()).thenReturn(true);
        return match;
    }
}