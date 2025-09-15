package com.matchday.modules.match.application;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.api.dto.dto.request.MatchUpdateRequest;
import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.domain.enums.SportsType;
import com.matchday.modules.match.exception.MatchControllerAdvice;
import com.matchday.modules.match.infrastructure.MatchRepository;
import com.matchday.modules.team.application.TeamUserService;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamType;
import com.matchday.modules.team.infrastructure.TeamRepository;
import com.matchday.modules.team.infrastructure.TeamUserRepository;
import com.matchday.modules.user.domain.User;
import com.matchday.modules.user.domain.enums.Gender;
import com.matchday.modules.team.domain.enums.Position;
import com.matchday.modules.user.domain.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchService - 매치 수정 테스트")
class MatchServiceUpdateTest {

    @InjectMocks
    private MatchService matchService;

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

    private User user;
    private Team team;
    private Match match;
    private MatchUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = User.createUser("test@test.com", "testuser", "Test User", 
                              LocalDate.of(1990, 1, 1), 180, Gender.MALE, 
                              Position.FW, UserRole.ROLE_MEMBER, "01012345678",
                              City.SEOUL, District.SEOUL_GANGNAM, false);
        ReflectionTestUtils.setField(user, "id", 1L);

        team = Team.createTeam("Test Team", "팀 설명", TeamType.CLUB, 
                              City.SEOUL, District.SEOUL_GANGNAM, "#FF0000", 
                              true, GroupGender.MIXED, 20, 
                              "국민은행", "123-456-789", "profile.jpg");
        ReflectionTestUtils.setField(team, "id", 1L);

        match = Match.createMatch(team, City.SEOUL, District.SEOUL_GANGNAM, "테스트 구장",
                                 LocalDate.now().plusDays(1), LocalTime.of(14, 0), LocalTime.of(16, 0),
                                 50000, MatchSize.ELEVEN, "12345", "#FF0000", true, 
                                 SportsType.SOCCER, "테스트 매치");
        ReflectionTestUtils.setField(match, "id", 1L);

        updateRequest = createUpdateRequest();
    }

    private MatchUpdateRequest createUpdateRequest() {
        MatchUpdateRequest request = new MatchUpdateRequest();
        ReflectionTestUtils.setField(request, "sportsType", "SOCCER");
        ReflectionTestUtils.setField(request, "matchSize", MatchSize.ELEVEN);
        ReflectionTestUtils.setField(request, "matchDateTime", LocalDateTime.now().plusDays(2).withHour(15));
        ReflectionTestUtils.setField(request, "endDateTime", LocalDateTime.now().plusDays(2).withHour(17));
        ReflectionTestUtils.setField(request, "fee", 60000);
        ReflectionTestUtils.setField(request, "uniformColor", "#0000FF");
        ReflectionTestUtils.setField(request, "hasBall", false);

        MatchUpdateRequest.MatchLocation location = new MatchUpdateRequest.MatchLocation();
        ReflectionTestUtils.setField(location, "zipCode", "54321");
        ReflectionTestUtils.setField(location, "address", "서울시 강남구 테헤란로 123");
        ReflectionTestUtils.setField(location, "detailAddress", "수정된 구장");
        ReflectionTestUtils.setField(request, "matchLocation", location);

        return request;
    }

    private MatchUpdateRequest createPartialUpdateRequest() {
        MatchUpdateRequest request = new MatchUpdateRequest();
        // 일부 필드만 설정
        ReflectionTestUtils.setField(request, "fee", 70000);
        ReflectionTestUtils.setField(request, "uniformColor", "#00FF00");
        ReflectionTestUtils.setField(request, "hasBall", true);
        return request;
    }

    @Test
    @DisplayName("매치 수정 성공")
    void updateMatch_성공() {
        // given
        given(matchRepository.findById(1L)).willReturn(Optional.of(match));
        doNothing().when(teamUserService).validatePermission(1L, 1L);
        
        AddressParsingService.AddressInfo addressInfo = 
            new AddressParsingService.AddressInfo(City.SEOUL, District.SEOUL_GANGNAM);
        given(addressParsingService.parseAddress(any())).willReturn(addressInfo);
        
        given(matchRepository.findConflictingMatches(any(), any(), any(), any()))
            .willReturn(Collections.emptyList());

        // when
        matchService.updateMatch(1L, 1L, updateRequest);

        // then
        verify(teamUserService).validatePermission(1L, 1L);
        verify(addressParsingService).parseAddress("서울시 강남구 테헤란로 123");
        verify(matchRepository).findConflictingMatches(
            eq(team), 
            eq(updateRequest.getMatchDateTime().toLocalDate()),
            eq(updateRequest.getMatchDateTime().toLocalTime()),
            eq(updateRequest.getEndDateTime().toLocalTime())
        );
        
        // 매치 정보가 업데이트되었는지 확인
        assertThat(match.getSportsType()).isEqualTo(SportsType.SOCCER);
        assertThat(match.getMatchSize()).isEqualTo(MatchSize.ELEVEN);
        assertThat(match.getFee()).isEqualTo(60000);
        assertThat(match.getHomeColor()).isEqualTo("#0000FF");
        assertThat(match.getHasBall()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 매치 수정 시 예외 발생")
    void updateMatch_매치없음_예외발생() {
        // given
        given(matchRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> matchService.updateMatch(999L, 1L, updateRequest))
            .isInstanceOf(MatchControllerAdvice.class)
            .hasFieldOrPropertyWithValue("errorCode", ResponseCode.MATCH_NOT_FOUND);
    }

    @Test
    @DisplayName("권한 없는 사용자가 매치 수정 시 예외 발생")
    void updateMatch_권한없음_예외발생() {
        // given
        given(matchRepository.findById(1L)).willReturn(Optional.of(match));
        doThrow(new RuntimeException("권한 없음")).when(teamUserService).validatePermission(1L, 999L);

        // when & then
        assertThatThrownBy(() -> matchService.updateMatch(1L, 999L, updateRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("권한 없음");
    }

    @Test
    @DisplayName("시간이 겹치는 매치가 있을 때 예외 발생")
    void updateMatch_시간겹침_예외발생() {
        // given
        given(matchRepository.findById(1L)).willReturn(Optional.of(match));
        doNothing().when(teamUserService).validatePermission(1L, 1L);
        
        // 다른 매치와 시간이 겹침
        given(matchRepository.findConflictingMatches(any(), any(), any(), any()))
            .willReturn(new java.util.ArrayList<>(java.util.List.of(2L))); // 다른 매치 ID

        // when & then
        assertThatThrownBy(() -> matchService.updateMatch(1L, 1L, updateRequest))
            .isInstanceOf(MatchControllerAdvice.class)
            .hasFieldOrPropertyWithValue("errorCode", ResponseCode.MATCH_DUPLICATED);
    }

    @Test
    @DisplayName("PENDING 상태가 아닌 매치 수정 시 예외 발생")
    void updateMatch_상태확정됨_예외발생() {
        // given
        // 매치 상태를 CONFIRMED로 변경
        ReflectionTestUtils.setField(match, "status", MatchStatus.CONFIRMED);
        
        given(matchRepository.findById(1L)).willReturn(Optional.of(match));
        doNothing().when(teamUserService).validatePermission(1L, 1L);

        // when & then
        assertThatThrownBy(() -> matchService.updateMatch(1L, 1L, updateRequest))
            .isInstanceOf(MatchControllerAdvice.class)
            .hasFieldOrPropertyWithValue("errorCode", ResponseCode.MATCH_ALREADY_COMPLETED);
    }

    @Test
    @DisplayName("현재 매치와 시간이 겹치는 경우는 제외하고 검증")
    void updateMatch_현재매치제외_성공() {
        // given
        given(matchRepository.findById(1L)).willReturn(Optional.of(match));
        doNothing().when(teamUserService).validatePermission(1L, 1L);
        
        AddressParsingService.AddressInfo addressInfo = 
            new AddressParsingService.AddressInfo(City.SEOUL, District.SEOUL_GANGNAM);
        given(addressParsingService.parseAddress(any())).willReturn(addressInfo);
        
        // 현재 수정 중인 매치 ID도 포함된 결과 반환
        given(matchRepository.findConflictingMatches(any(), any(), any(), any()))
            .willReturn(new java.util.ArrayList<>(java.util.List.of(1L))); // 현재 매치 ID만 반환

        // when
        matchService.updateMatch(1L, 1L, updateRequest);

        // then
        // 현재 매치 ID는 제외되므로 예외가 발생하지 않아야 함
        verify(matchRepository).findConflictingMatches(any(), any(), any(), any());
    }

    @Test
    @DisplayName("부분 필드만 수정하는 경우 성공")
    void updateMatch_부분필드수정_성공() {
        // given
        MatchUpdateRequest partialRequest = createPartialUpdateRequest();
        given(matchRepository.findById(1L)).willReturn(Optional.of(match));
        doNothing().when(teamUserService).validatePermission(1L, 1L);

        Integer originalFee = match.getFee();
        String originalColor = match.getHomeColor();
        Boolean originalHasBall = match.getHasBall();

        // when
        matchService.updateMatch(1L, 1L, partialRequest);

        // then
        verify(teamUserService).validatePermission(1L, 1L);
        verify(matchRepository).findById(1L); // findById는 호출됨
        verifyNoInteractions(addressParsingService); // 장소 수정이 없으므로 호출되지 않음
        verifyNoMoreInteractions(matchRepository); // findById 외에는 호출되지 않음
        
        // 수정된 필드만 확인
        assertThat(match.getFee()).isEqualTo(70000);
        assertThat(match.getHomeColor()).isEqualTo("#00FF00");
        assertThat(match.getHasBall()).isTrue();
        
        // 수정되지 않은 필드는 기존 값 유지
        assertThat(match.getSportsType()).isEqualTo(SportsType.SOCCER);
        assertThat(match.getMatchSize()).isEqualTo(MatchSize.ELEVEN);
    }

    @Test
    @DisplayName("null 값들은 무시하고 업데이트")
    void updateMatch_null값무시_성공() {
        // given
        MatchUpdateRequest emptyRequest = new MatchUpdateRequest();
        given(matchRepository.findById(1L)).willReturn(Optional.of(match));
        doNothing().when(teamUserService).validatePermission(1L, 1L);

        SportsType originalSportsType = match.getSportsType();
        MatchSize originalMatchSize = match.getMatchSize();
        Integer originalFee = match.getFee();

        // when
        matchService.updateMatch(1L, 1L, emptyRequest);

        // then
        verify(teamUserService).validatePermission(1L, 1L);
        verify(matchRepository).findById(1L); // findById는 호출됨
        verifyNoInteractions(addressParsingService);
        verifyNoMoreInteractions(matchRepository); // findById 외에는 호출되지 않음
        
        // 모든 필드가 기존 값 유지
        assertThat(match.getSportsType()).isEqualTo(originalSportsType);
        assertThat(match.getMatchSize()).isEqualTo(originalMatchSize);
        assertThat(match.getFee()).isEqualTo(originalFee);
    }
}