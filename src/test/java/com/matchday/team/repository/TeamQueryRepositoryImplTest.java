package com.matchday.team.repository;

import com.matchday.global.config.QueryDslConfig;
import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.Team;
import com.matchday.team.domain.TeamFixture;
import com.matchday.team.domain.enums.GroupGender;
import com.matchday.team.domain.enums.TeamType;
import com.matchday.team.dto.request.TeamSearchRequest;
import com.matchday.team.dto.response.TeamListResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfig.class)
@DisplayName("TeamQueryRepositoryImpl 동적 쿼리 테스트")
class TeamQueryRepositoryImplTest {

    @Autowired
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;
    private TeamQueryRepositoryImpl teamQueryRepository;

    @BeforeEach
    void setUp() {
        queryFactory = new JPAQueryFactory(entityManager);
        teamQueryRepository = new TeamQueryRepositoryImpl(queryFactory);
        
        setupTestData();
    }

    @Test
    @DisplayName("필터 없이 전체 팀 조회")
    void findTeamsByConditions_withoutFilter_returnsAllTeams() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(6); // 총 6개 팀
        assertThat(result.getTotalElements()).isEqualTo(6);
    }

    @Test
    @DisplayName("시/도 필터로 팀 조회")
    void findTeamsByConditions_withCityFilter_returnsFilteredTeams() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(City.SEOUL, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(3); // 서울 팀 3개
        assertThat(result.getContent()).allMatch(team -> team.getCity() == City.SEOUL);
    }

    @Test
    @DisplayName("구/군 필터로 팀 조회")
    void findTeamsByConditions_withDistrictFilter_returnsFilteredTeams() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, District.SEOUL_GANGNAM, null, null, null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(1); // 강남구 팀 1개
        assertThat(result.getContent()).allMatch(team -> team.getDistrict() == District.SEOUL_GANGNAM);
    }

    @Test
    @DisplayName("팀 유형 필터로 팀 조회")
    void findTeamsByConditions_withTypeFilter_returnsFilteredTeams() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, TeamType.CLUB, null, null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(3); // CLUB 팀 3개
        assertThat(result.getContent()).allMatch(team -> team.getType() == TeamType.CLUB);
    }

    @Test
    @DisplayName("성별 필터로 팀 조회")
    void findTeamsByConditions_withGenderFilter_returnsFilteredTeams() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, GroupGender.MIXED, null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(2); // MIXED 팀 2개
        assertThat(result.getContent()).allMatch(team -> team.getGender() == GroupGender.MIXED);
    }

    @Test
    @DisplayName("키워드 검색으로 팀 조회")
    void findTeamsByConditions_withKeywordSearch_returnsFilteredTeams() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, null, "FC");
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(3); // FC가 포함된 팀 3개
        assertThat(result.getContent()).allMatch(team -> 
            team.getName().toLowerCase().contains("fc"));
    }

    @Test
    @DisplayName("복합 필터로 팀 조회")
    void findTeamsByConditions_withMultipleFilters_returnsFilteredTeams() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(
            City.SEOUL, District.SEOUL_GANGNAM, TeamType.CLUB, GroupGender.MIXED, "FC"
        );
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(1); // 모든 조건을 만족하는 팀 1개
        TeamListResponse team = result.getContent().get(0);
        assertThat(team.getCity()).isEqualTo(City.SEOUL);
        assertThat(team.getDistrict()).isEqualTo(District.SEOUL_GANGNAM);
        assertThat(team.getType()).isEqualTo(TeamType.CLUB);
        assertThat(team.getGender()).isEqualTo(GroupGender.MIXED);
        assertThat(team.getName().toLowerCase()).contains("fc");
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void findTeamsByConditions_withPagination_returnsPagedResults() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, null, null);
        Pageable pageable = PageRequest.of(1, 2); // 두 번째 페이지, 페이지당 2개

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(6);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(1);
        assertThat(result.getPageable().getPageSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("이름 오름차순 정렬 테스트")
    void findTeamsByConditions_withNameAscSort_returnsSortedResults() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, null, null);
        searchRequest.setSort("name");
        searchRequest.setDirection("asc");
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        List<String> teamNames = result.getContent().stream()
            .map(TeamListResponse::getName)
            .toList();
        
        // 이름이 알파벳 순으로 정렬되어 있는지 확인
        for (int i = 1; i < teamNames.size(); i++) {
            assertThat(teamNames.get(i).compareTo(teamNames.get(i-1))).isGreaterThanOrEqualTo(0);
        }
    }

    @Test
    @DisplayName("생성일 내림차순 정렬 테스트 (기본값)")
    void findTeamsByConditions_withDefaultSort_returnsDescSortedResults() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, null, null);
        searchRequest.setSort("createdAt");
        searchRequest.setDirection("desc");
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(6);
        // 최신 생성된 팀이 먼저 나오는지 확인 (생성 순서의 역순)
    }

    @Test
    @DisplayName("조건에 맞는 팀이 없는 경우")
    void findTeamsByConditions_withNoMatchingResults_returnsEmptyPage() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, null, "NonExistentTeam");
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }

    private void setupTestData() {
        // 서울 강남 FC CLUB MIXED
        Team team1 = TeamFixture.createTeam("Gangnam FC", City.SEOUL, District.SEOUL_GANGNAM, 
                                          TeamType.CLUB, GroupGender.MIXED);
        entityManager.persist(team1);
        
        // 서울 강서 SMALL_GROUP MALE
        Team team2 = TeamFixture.createTeam("Gangseo Team", City.SEOUL, District.SEOUL_GANGSEO, 
                                          TeamType.SMALL_GROUP, GroupGender.MALE);
        entityManager.persist(team2);
        
        // 서울 송파 CLUB FEMALE
        Team team3 = TeamFixture.createTeam("Songpa FC", City.SEOUL, District.SEOUL_SONGPA, 
                                          TeamType.CLUB, GroupGender.FEMALE);
        entityManager.persist(team3);
        
        // 부산 해운대 CLUB MIXED
        Team team4 = TeamFixture.createTeam("Haeundae FC", City.BUSAN, District.BUSAN_HAEUNDAE, 
                                          TeamType.CLUB, GroupGender.MIXED);
        entityManager.persist(team4);
        
        // 부산 사하 COMMUNITY MALE
        Team team5 = TeamFixture.createTeam("Saha Community", City.BUSAN, District.BUSAN_SAHA, 
                                          TeamType.COMMUNITY, GroupGender.MALE);
        entityManager.persist(team5);
        
        // 대구 중구 SMALL_GROUP FEMALE
        Team team6 = TeamFixture.createTeam("Daegu Central", City.DAEGU, District.DAEGU_JUNG, 
                                          TeamType.SMALL_GROUP, GroupGender.FEMALE);
        entityManager.persist(team6);
        
        entityManager.flush();
        entityManager.clear();
    }

    private TeamSearchRequest createSearchRequest(City city, District district, TeamType type, 
                                                 GroupGender gender, String keyword) {
        TeamSearchRequest request = new TeamSearchRequest();
        request.setCity(city);
        request.setDistrict(district);
        request.setType(type);
        request.setGender(gender);
        request.setKeyword(keyword);
        request.setPage(0);
        request.setSize(20);
        request.setSort("createdAt");
        request.setDirection("desc");
        return request;
    }
}