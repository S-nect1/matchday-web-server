package com.matchday.modules.match.application;

import com.matchday.common.dto.response.PagedResponse;
import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.api.dto.projection.TeamConfirmedMatchProjection;
import com.matchday.modules.match.api.dto.projection.TeamConfirmedMatchProjectionImpl;
import com.matchday.modules.match.api.dto.request.MatchCreateRequest;
import com.matchday.modules.match.api.dto.request.MatchUpdateRequest;
import com.matchday.modules.match.api.dto.request.MatchResultRequest;
import com.matchday.modules.match.api.dto.response.MatchListResponse;
import com.matchday.modules.match.api.dto.response.MatchResponse;
import com.matchday.modules.match.api.dto.response.MonthlyMatchResponse;
import com.matchday.modules.match.api.dto.response.TeamConfirmedMatchResponse;
import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.MatchFixture;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.domain.enums.SportsType;
import com.matchday.modules.match.exception.MatchControllerAdvice;
import com.matchday.modules.match.infrastructure.MatchRepository;
import com.matchday.modules.team.application.TeamUserService;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.domain.TeamUser;
import com.matchday.modules.team.domain.TeamFixture;
import com.matchday.modules.team.infrastructure.TeamRepository;
import com.matchday.modules.team.infrastructure.TeamUserRepository;
import com.matchday.modules.user.domain.User;
import com.matchday.modules.user.user.domain.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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

    private User testUser;
    private Team testTeam;
    private TeamUser testTeamUser;
    private Match testMatch;

    @BeforeEach
    void setUp() {
        // 공통 테스트 데이터 설정
        testUser = UserFixture.defaultUser1();
        testTeam = TeamFixture.defaultTeam();
        testTeamUser = TeamUser.createLeader(testTeam, testUser);
        testMatch = MatchFixture.matchWithTeam(testTeam);
        
        // 테스트 데이터에 ID 설정
        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testTeam, "id", 1L);
        ReflectionTestUtils.setField(testMatch, "id", 1L);
    }

    @Nested
    @DisplayName("매치 생성")
    class CreateMatch {

        @Test
        @DisplayName("매치 생성이 성공한다")
        void createMatch_success() {
            // given
            MatchCreateRequest request = createMatchCreateRequest();
            AddressParsingService.AddressInfo addressInfo = new AddressParsingService.AddressInfo(City.SEOUL, District.SEOUL_GANGNAM);
            
            given(teamRepository.findById(1L)).willReturn(Optional.of(testTeam));
            given(addressParsingService.parseAddress(anyString())).willReturn(addressInfo);
            given(matchRepository.findConflictingMatches(any(), any(), any(), any())).willReturn(List.of());
            given(matchRepository.save(any(Match.class))).willReturn(testMatch);
            // teamUserService.validatePermission은 void이므로 성공 케이스에서는 아무것도 하지 않음

            // when
            Long result = matchService.createMatch(1L, request);

            // then
            assertThat(result).isEqualTo(1L);
            verify(teamUserService).validatePermission(1L, 1L);
            verify(matchRepository).save(any(Match.class));
        }

        @Test
        @DisplayName("시간이 겹치는 매치가 있으면 예외가 발생한다")
        void createMatch_conflictingTime_throwsException() {
            // given
            MatchCreateRequest request = createMatchCreateRequest();
            AddressParsingService.AddressInfo addressInfo = new AddressParsingService.AddressInfo(City.SEOUL, District.SEOUL_GANGNAM);
            
            given(teamRepository.findById(1L)).willReturn(Optional.of(testTeam));
            given(addressParsingService.parseAddress(anyString())).willReturn(addressInfo);
            given(matchRepository.findConflictingMatches(any(), any(), any(), any())).willReturn(List.of(2L));

            // when & then
            assertThatThrownBy(() -> matchService.createMatch(1L, request))
                    .isInstanceOf(MatchControllerAdvice.class);
        }

        private MatchCreateRequest createMatchCreateRequest() {
            MatchCreateRequest request = new MatchCreateRequest();
            LocalDateTime futureDate = LocalDateTime.now().plusDays(7);
            ReflectionTestUtils.setField(request, "teamId", 1L);
            ReflectionTestUtils.setField(request, "matchDateTime", futureDate);
            ReflectionTestUtils.setField(request, "endDateTime", futureDate.plusHours(2));
            ReflectionTestUtils.setField(request, "fee", 100000);
            ReflectionTestUtils.setField(request, "matchSize", MatchSize.SIX);
            ReflectionTestUtils.setField(request, "sportsType", "FUTSAL");
            ReflectionTestUtils.setField(request, "uniformColor", "RED");
            ReflectionTestUtils.setField(request, "hasBall", true);
            
            MatchCreateRequest.MatchLocation location = new MatchCreateRequest.MatchLocation();
            ReflectionTestUtils.setField(location, "address", "서울시 강남구");
            ReflectionTestUtils.setField(location, "detailAddress", "테스트 경기장");
            ReflectionTestUtils.setField(location, "zipCode", "12345");
            ReflectionTestUtils.setField(request, "matchLocation", location);
            
            return request;
        }
    }

    @Nested
    @DisplayName("매치 조회")
    class GetMatch {

        @Test
        @DisplayName("매치 상세 조회가 성공한다")
        void getMatchDetails_success() {
            // given
            given(matchRepository.findById(1L)).willReturn(Optional.of(testMatch));

            // when
            MatchResponse result = matchService.getMatchDetails(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getMatchId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 매치 조회시 예외가 발생한다")
        void getMatchDetails_notFound_throwsException() {
            // given
            given(matchRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchService.getMatchDetails(1L))
                    .isInstanceOf(MatchControllerAdvice.class);
        }

        @Test
        @DisplayName("팀 매치 목록 조회가 성공한다")
        void getTeamMatches_success() {
            // given
            given(teamRepository.findById(1L)).willReturn(Optional.of(testTeam));
            given(teamUserRepository.findByTeamIdAndUserId(1L, 1L)).willReturn(Optional.of(testTeamUser));
            given(matchRepository.findByHomeTeamOrderByCreatedAtDesc(testTeam)).willReturn(List.of(testMatch));

            // when
            List<MatchListResponse> result = matchService.getTeamMatches(1L, 1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("매치 수정")
    class UpdateMatch {

        @Test
        @DisplayName("매치 수정이 성공한다")
        void updateMatch_success() {
            // given
            MatchUpdateRequest request = createMatchUpdateRequest();
            AddressParsingService.AddressInfo addressInfo = new AddressParsingService.AddressInfo(City.SEOUL, District.SEOUL_GANGDONG);
            
            given(matchRepository.findById(1L)).willReturn(Optional.of(testMatch));
            given(addressParsingService.parseAddress(anyString())).willReturn(addressInfo);
            given(matchRepository.findConflictingMatches(any(), any(), any(), any())).willReturn(List.of(1L));

            // when
            matchService.updateMatch(1L, 1L, request);

            // then
            verify(teamUserService).validatePermission(1L, 1L);
        }

        @Test
        @DisplayName("권한이 없는 사용자의 매치 수정시 예외가 발생한다")
        void updateMatch_forbidden_throwsException() {
            // given
            MatchUpdateRequest request = createMatchUpdateRequest();
            given(matchRepository.findById(1L)).willReturn(Optional.of(testMatch));

            // when & then
            assertThatThrownBy(() -> matchService.updateMatch(1L, 2L, request))
                    .isInstanceOf(Exception.class);

            verify(teamUserService).validatePermission(1L, 2L);
        }

        private MatchUpdateRequest createMatchUpdateRequest() {
            MatchUpdateRequest request = new MatchUpdateRequest();
            LocalDateTime futureDate = LocalDateTime.now().plusDays(10);
            ReflectionTestUtils.setField(request, "sportsType", "FUTSAL");
            ReflectionTestUtils.setField(request, "matchSize", MatchSize.FIVE);
            ReflectionTestUtils.setField(request, "matchDateTime", futureDate);
            ReflectionTestUtils.setField(request, "endDateTime", futureDate.plusHours(2));
            ReflectionTestUtils.setField(request, "fee", 120000);
            ReflectionTestUtils.setField(request, "uniformColor", "BLUE");
            ReflectionTestUtils.setField(request, "hasBall", false);
            
            MatchUpdateRequest.MatchLocation location = new MatchUpdateRequest.MatchLocation();
            ReflectionTestUtils.setField(location, "address", "서울시 강동구");
            ReflectionTestUtils.setField(location, "detailAddress", "업데이트 경기장");
            ReflectionTestUtils.setField(location, "zipCode", "54321");
            ReflectionTestUtils.setField(request, "matchLocation", location);
            
            return request;
        }
    }

    @Nested
    @DisplayName("매치 취소")
    class CancelMatch {

        @Test
        @DisplayName("매치 취소가 성공한다")
        void cancelMatch_success() {
            // given
            given(matchRepository.findById(1L)).willReturn(Optional.of(testMatch));

            // when
            matchService.cancelMatch(1L, 1L);

            // then
            verify(teamUserService).validatePermission(1L, 1L);
        }

        @Test
        @DisplayName("존재하지 않는 매치 취소시 예외가 발생한다")
        void cancelMatch_notFound_throwsException() {
            // given
            given(matchRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchService.cancelMatch(1L, 1L))
                    .isInstanceOf(MatchControllerAdvice.class);
        }
    }

    @Nested
    @DisplayName("월별 매치 조회")
    class GetMonthlyMatches {

        @Test
        @DisplayName("월별 매치 조회가 성공한다")
        void getMonthlyMatches_success() {
            // given
            Long userId = 1L;
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 1, 31);

            // 예상되는 월별 매치 응답 생성 (일자별 매치 ID 맵)
            java.util.Map<Integer, List<Long>> dailyMatches = new java.util.HashMap<>();
            dailyMatches.put(15, List.of(1L));
            dailyMatches.put(20, List.of(2L));
            MonthlyMatchResponse expectedResponse = MonthlyMatchResponse.of(dailyMatches);

            given(matchRepository.findMonthlyMatches(userId, startDate, endDate)).willReturn(expectedResponse);

            // when
            MonthlyMatchResponse result = matchService.getMonthlyMatches(userId, startDate, endDate);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getDailyMatches()).hasSize(2);
            assertThat(result.getDailyMatches().get(15)).containsExactly(1L);
            assertThat(result.getDailyMatches().get(20)).containsExactly(2L);
        }

        @Test
        @DisplayName("매치가 없을 경우 빈 목록을 반환한다")
        void getMonthlyMatches_emptyResult() {
            // given
            Long userId = 1L;
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 1, 31);

            // 빈 월별 매치 응답 생성
            MonthlyMatchResponse emptyResponse = MonthlyMatchResponse.of(new java.util.HashMap<>());
            given(matchRepository.findMonthlyMatches(userId, startDate, endDate)).willReturn(emptyResponse);

            // when
            MonthlyMatchResponse result = matchService.getMonthlyMatches(userId, startDate, endDate);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getDailyMatches()).isEmpty();
        }
    }

    @Nested
    @DisplayName("팀 확정 매치 조회")
    class GetTeamConfirmedMatches {

        @Test
        @DisplayName("팀의 확정된 매치 목록을 성공적으로 조회한다")
        void getTeamConfirmedMatches_success() {
            // given
            Long userId = 1L;
            Long teamId = 10L;
            int page = 0;
            int size = 20;
            String sortBy = "date";
            String direction = "desc";

            TeamUser teamUser = Mockito.mock(TeamUser.class);
            given(teamUserRepository.findByTeamIdAndUserId(teamId, userId))
                    .willReturn(Optional.of(teamUser));

            TeamConfirmedMatchProjection homeMatch = new TeamConfirmedMatchProjectionImpl(
                    1L, "FC Opponent", "HOME",
                    City.SEOUL, District.SEOUL_GANGNAM,
                    "테스트 경기장", LocalDate.of(2024, 1, 15),
                    LocalTime.of(14, 0), LocalTime.of(16, 0),
                    100000, MatchSize.SIX, SportsType.FUTSAL,
                    MatchStatus.CONFIRMED, 2, 1
            );

            TeamConfirmedMatchProjection awayMatch = new TeamConfirmedMatchProjectionImpl(
                    2L, "FC Home", "AWAY",
                    City.SEOUL, District.SEOUL_GANGDONG,
                    "어웨이 경기장", LocalDate.of(2024, 1, 20),
                    LocalTime.of(18, 0), LocalTime.of(20, 0),
                    150000, MatchSize.FIVE, SportsType.FUTSAL,
                    MatchStatus.COMPLETED, 1, 3
            );

            List<TeamConfirmedMatchProjection> mockMatches = List.of(homeMatch, awayMatch);
            Page<TeamConfirmedMatchProjection> mockPage = new PageImpl<>(
                    mockMatches, 
                    PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy)), 
                    2
            );

            given(matchRepository.findTeamConfirmedMatches(eq(teamId), any())).willReturn(mockPage);

            // when
            PagedResponse<TeamConfirmedMatchResponse> result =
                    matchService.getTeamConfirmedMatches(userId, teamId, page, size, sortBy, direction);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.isFirst()).isTrue();
            assertThat(result.isLast()).isTrue();

            TeamConfirmedMatchResponse firstMatch = result.getContent().get(0);
            assertThat(firstMatch.getMatchId()).isEqualTo(1L);
            assertThat(firstMatch.getOpponentTeamName()).isEqualTo("FC Opponent");
            assertThat(firstMatch.getMyTeamRole()).isEqualTo("HOME");
        }

        @Test
        @DisplayName("팀 멤버가 아닌 경우 권한 없음 예외가 발생한다")
        void getTeamConfirmedMatches_forbidden() {
            // given
            Long userId = 1L;
            Long teamId = 10L;

            given(teamUserRepository.findByTeamIdAndUserId(teamId, userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchService.getTeamConfirmedMatches(userId, teamId, 0, 20, "date", "desc"))
                    .isInstanceOf(MatchControllerAdvice.class)
                    .hasMessage(ResponseCode._FORBIDDEN.getMessage());
        }

    }

    @Nested
    @DisplayName("매치 결과 기록")
    class RecordMatchResult {

        private Match confirmedMatch;

        @BeforeEach
        void setUpConfirmedMatch() {
            confirmedMatch = MatchFixture.matchWithTeam(testTeam);
            ReflectionTestUtils.setField(confirmedMatch, "id", 1L);
            ReflectionTestUtils.setField(confirmedMatch, "status", MatchStatus.CONFIRMED);

            ReflectionTestUtils.setField(confirmedMatch, "awayTeam", testTeam);
            ReflectionTestUtils.setField(confirmedMatch.getHomeTeam(), "id", 10L);
            ReflectionTestUtils.setField(testTeam, "id", 20L);
            // 매치 종료 시간을 1시간 전으로 설정 (48시간 이내)
            ReflectionTestUtils.setField(confirmedMatch, "date", LocalDate.now().minusDays(0));
            ReflectionTestUtils.setField(confirmedMatch, "endTime", LocalTime.now().minusHours(1));
        }

        @Test
        @DisplayName("매치 결과 기록이 성공한다")
        void recordMatchResult_success() {
            // given
            MatchResultRequest request = createMatchResultRequest(3, 2);

            given(matchRepository.findById(1L)).willReturn(Optional.of(confirmedMatch));

            // when
            matchService.recordMatchResult(1L, 1L, request);

            // then
            // 매치가 완료 상태로 변경되었는지 확인
            assertThat(confirmedMatch.getStatus()).isEqualTo(MatchStatus.COMPLETED);
            assertThat(confirmedMatch.getHomeScore()).isEqualTo(3);
            assertThat(confirmedMatch.getAwayScore()).isEqualTo(2);
        }

        @Test
        @DisplayName("존재하지 않는 매치에 결과 기록시 예외가 발생한다")
        void recordMatchResult_matchNotFound_throwsException() {
            // given
            MatchResultRequest request = createMatchResultRequest(2, 1);
            given(matchRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchService.recordMatchResult(1L, 1L, request))
                    .isInstanceOf(MatchControllerAdvice.class)
                    .hasMessage(ResponseCode.MATCH_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("확정되지 않은 매치에 결과 기록시 예외가 발생한다")
        void recordMatchResult_notConfirmedMatch_throwsException() {
            // given
            MatchResultRequest request = createMatchResultRequest(2, 1);
            Match pendingMatch = MatchFixture.matchWithTeam(testTeam);
            ReflectionTestUtils.setField(pendingMatch, "status", MatchStatus.PENDING);
            
            given(matchRepository.findById(1L)).willReturn(Optional.of(pendingMatch));

            // when & then
            assertThatThrownBy(() -> matchService.recordMatchResult(1L, 1L, request))
                    .isInstanceOf(MatchControllerAdvice.class)
                    .hasMessage(ResponseCode.MATCH_NOT_CONFIRMED.getMessage());
        }

        @Test
        @DisplayName("운영진이 아닌 경우 예외가 발생한다")
        void recordMatchResult_notParticipant_throwsException() {
            // given
            MatchResultRequest request = createMatchResultRequest(2, 1);
            
            given(matchRepository.findById(1L)).willReturn(Optional.of(confirmedMatch));

            Long homeId = confirmedMatch.getHomeTeam().getId();
            Long awayId = confirmedMatch.getAwayTeam().getId();
            Long userId = 1L;
            doThrow(new MatchControllerAdvice(ResponseCode._FORBIDDEN))
                    .when(teamUserService)
                    .validateBothPermission(eq(homeId), eq(awayId), eq(userId));

            // when & then
            assertThatThrownBy(() -> matchService.recordMatchResult(1L, 1L, request))
                    .isInstanceOf(MatchControllerAdvice.class)
                    .hasMessage(ResponseCode._FORBIDDEN.getMessage());
        }

        @Test
        @DisplayName("48시간이 지난 매치에 결과 기록시 예외가 발생한다")
        void recordMatchResult_deadlinePassed_throwsException() {
            // given
            MatchResultRequest request = createMatchResultRequest(2, 1);
            Match expiredMatch = MatchFixture.matchWithTeam(testTeam);
            ReflectionTestUtils.setField(expiredMatch, "id", 1L);
            ReflectionTestUtils.setField(expiredMatch, "awayTeam", testTeam);
            ReflectionTestUtils.setField(expiredMatch, "status", MatchStatus.CONFIRMED);
            // 매치 종료 시간을 49시간 전으로 설정 (48시간 초과)
            ReflectionTestUtils.setField(expiredMatch, "date", LocalDate.now().minusDays(3));
            ReflectionTestUtils.setField(expiredMatch, "endTime", LocalTime.now().minusHours(1));
            
            given(matchRepository.findById(1L)).willReturn(Optional.of(expiredMatch));
//            given(teamUserRepository.findByTeamIdAndUserId(1L, 1L)).willReturn(Optional.of(testTeamUser));

            // when & then
            assertThatThrownBy(() -> matchService.recordMatchResult(1L, 1L, request))
                    .isInstanceOf(MatchControllerAdvice.class)
                    .hasMessage(ResponseCode.MATCH_TIME_OUT.getMessage());
        }

        @Test
        @DisplayName("음수 점수로 결과 기록시 예외가 발생한다")
        void recordMatchResult_invalidScore_throwsException() {
            // given
            MatchResultRequest request = createMatchResultRequest(-1, 2);
            
            given(matchRepository.findById(1L)).willReturn(Optional.of(confirmedMatch));
//            given(teamUserRepository.findByTeamIdAndUserId(1L, 1L)).willReturn(Optional.of(testTeamUser));

            // when & then
            assertThatThrownBy(() -> matchService.recordMatchResult(1L, 1L, request))
                    .isInstanceOf(MatchControllerAdvice.class)
                    .hasMessage(ResponseCode.MATCH_INVALID_SCORE.getMessage());
        }

        private MatchResultRequest createMatchResultRequest(Integer homeScore, Integer awayScore) {
            MatchResultRequest request = new MatchResultRequest();
            ReflectionTestUtils.setField(request, "homeScore", homeScore);
            ReflectionTestUtils.setField(request, "awayScore", awayScore);
            return request;
        }
    }
}