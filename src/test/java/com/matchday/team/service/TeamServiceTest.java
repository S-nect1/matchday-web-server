package com.matchday.team.service;

import com.matchday.global.dto.response.PagedResponse;
import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.Team;
import com.matchday.team.domain.TeamFixture;
import com.matchday.team.domain.enums.GroupGender;
import com.matchday.team.domain.enums.TeamType;
import com.matchday.team.dto.request.TeamSearchRequest;
import com.matchday.team.dto.response.TeamListResponse;
import com.matchday.team.repository.TeamRepository;
import com.matchday.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamService 팀 목록 조회 테스트")
class TeamServiceTest {

    @Mock
    TeamRepository teamRepository;
    @InjectMocks
    TeamService teamService;

    @Captor
    ArgumentCaptor<Pageable> pageableCap;

    @Test
    void delegatesAndWraps() {
        TeamSearchRequest req = new TeamSearchRequest();
        req.setPage(0);
        req.setSize(20);

        Page<TeamListResponse> stub = Page.empty(PageRequest.of(0, 20));
        given(teamRepository.findTeamsByConditions(eq(req), any(Pageable.class))).willReturn(stub);

        PagedResponse<TeamListResponse> out = teamService.getTeamList(req);

        verify(teamRepository).findTeamsByConditions(eq(req), pageableCap.capture());
        Pageable p = pageableCap.getValue();
        assertThat(p.getPageNumber()).isEqualTo(0);
        assertThat(p.getPageSize()).isEqualTo(20);
        assertThat(out.getTotalElements()).isEqualTo(0);
    }
}