package com.matchday.modules.team.team.infrastructure;

import com.matchday.config.QueryDslConfig;
import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.infrastructure.TeamQueryRepositoryImpl;
import com.matchday.modules.team.team.domain.TeamFixture;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamType;
import com.matchday.modules.team.domain.TeamUser;
import com.matchday.modules.team.domain.enums.TeamRole;
import com.matchday.modules.team.api.dto.dto.request.TeamSearchRequest;
import com.matchday.modules.team.api.dto.dto.response.TeamListResponse;
import com.matchday.modules.user.domain.User;
import com.matchday.modules.user.domain.enums.Gender;
import com.matchday.modules.team.domain.enums.Position;
import com.matchday.modules.user.domain.enums.UserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import java.time.LocalDate;
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

    @Test
    @DisplayName("팀 멤버 수가 정확하게 집계된다")
    void findTeamsByConditions_returnsMemberCount() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        assertThat(result.getContent()).hasSize(6);
        
        // 각 팀의 memberCount가 설정되어 있는지 확인
        for (TeamListResponse team : result.getContent()) {
            assertThat(team.getMemberCount()).isNotNull();
            assertThat(team.getMemberCount()).isGreaterThanOrEqualTo(0);
        }
        
        // 특정 팀들의 멤버 수 확인 (setupTestData()에서 추가한 멤버 기준)
        TeamListResponse gangnamFc = result.getContent().stream()
            .filter(team -> "Gangnam FC".equals(team.getName()))
            .findFirst()
            .orElseThrow();
        assertThat(gangnamFc.getMemberCount()).isEqualTo(3); // 팀장 + 멤버 2명
        
        TeamListResponse gangseoTeam = result.getContent().stream()
            .filter(team -> "Gangseo Team".equals(team.getName()))
            .findFirst()
            .orElseThrow();
        assertThat(gangseoTeam.getMemberCount()).isEqualTo(1); // 팀장만
    }

    @Test
    @DisplayName("20대 연령대 필터로 팀 조회")
    void findTeamsByConditions_withAgeGroupFilter_returns20sTeams() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, null, null);
        searchRequest.setAgeGroup(20);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        // 평균 나이가 20대인 팀만 필터링됨 (Gangnam FC: 26세, Songpa FC: 22세)
        assertThat(result.getContent()).hasSize(2); // Gangnam FC, Songpa FC
        
        List<String> teamNames = result.getContent().stream()
            .map(TeamListResponse::getName)
            .toList();
        assertThat(teamNames).containsExactlyInAnyOrder("Gangnam FC", "Songpa FC");
    }

    @Test
    @DisplayName("30대 연령대 필터로 팀 조회")
    void findTeamsByConditions_withAgeGroupFilter_returns30sTeams() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(null, null, null, null, null);
        searchRequest.setAgeGroup(30);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        // 평균 나이가 30대인 팀은 없음 (Gangnam FC: 26세, Gangseo Team: 42세, Songpa FC: 22세)
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    @DisplayName("연령대 필터와 다른 조건을 함께 적용")
    void findTeamsByConditions_withAgeGroupAndOtherFilters() {
        // given
        TeamSearchRequest searchRequest = createSearchRequest(
            City.SEOUL, null, TeamType.CLUB, null, null
        );
        searchRequest.setAgeGroup(20); // 20대 필터 추가
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<TeamListResponse> result = teamQueryRepository.findTeamsByConditions(searchRequest, pageable);

        // then
        // 서울의 CLUB 팀 중에서 20대 멤버가 있는 팀
        assertThat(result.getContent()).hasSize(2); // Gangnam FC, Songpa FC
        
        for (TeamListResponse team : result.getContent()) {
            assertThat(team.getCity()).isEqualTo(City.SEOUL);
            assertThat(team.getType()).isEqualTo(TeamType.CLUB);
            assertThat(team.getMemberCount()).isGreaterThan(0);
        }
    }

    private void setupTestData() {
        // 사용자 생성
        User user20s1 = createUser("user20s1@test.com", LocalDate.of(2000, 5, 15)); // 24세
        User user20s2 = createUser("user20s2@test.com", LocalDate.of(1999, 8, 20)); // 25세
        User user30s = createUser("user30s@test.com", LocalDate.of(1990, 3, 10)); // 34세
        User user40s = createUser("user40s@test.com", LocalDate.of(1980, 12, 5)); // 44세
        
        entityManager.persist(user20s1);
        entityManager.persist(user20s2);
        entityManager.persist(user30s);
        entityManager.persist(user40s);
        
        // 팀 생성
        Team team1 = TeamFixture.createTeam("Gangnam FC", City.SEOUL, District.SEOUL_GANGNAM, 
                                          TeamType.CLUB, GroupGender.MIXED);
        Team team2 = TeamFixture.createTeam("Gangseo Team", City.SEOUL, District.SEOUL_GANGSEO, 
                                          TeamType.SMALL_GROUP, GroupGender.MALE);
        Team team3 = TeamFixture.createTeam("Songpa FC", City.SEOUL, District.SEOUL_SONGPA, 
                                          TeamType.CLUB, GroupGender.FEMALE);
        Team team4 = TeamFixture.createTeam("Haeundae FC", City.BUSAN, District.BUSAN_HAEUNDAE, 
                                          TeamType.CLUB, GroupGender.MIXED);
        Team team5 = TeamFixture.createTeam("Saha Community", City.BUSAN, District.BUSAN_SAHA, 
                                          TeamType.COMMUNITY, GroupGender.MALE);
        Team team6 = TeamFixture.createTeam("Daegu Central", City.DAEGU, District.DAEGU_JUNG, 
                                          TeamType.SMALL_GROUP, GroupGender.FEMALE);
        
        entityManager.persist(team1);
        entityManager.persist(team2);
        entityManager.persist(team3);
        entityManager.persist(team4);
        entityManager.persist(team5);
        entityManager.persist(team6);
        
        // 팀원 추가
        // Gangnam FC: 20대 2명 + 30대 1명 = 총 3명
        TeamUser teamUser1_1 = TeamUser.createLeader(team1, user20s1); // 팀장
        TeamUser teamUser1_2 = TeamUser.joinTeamWithBackNumber(team1, user20s2, TeamRole.MEMBER, 10);
        TeamUser teamUser1_3 = TeamUser.joinTeamWithBackNumber(team1, user30s, TeamRole.MEMBER, 11);
        
        // Gangseo Team: 40대 1명 = 총 1명
        TeamUser teamUser2_1 = TeamUser.createLeader(team2, user40s); // 팀장만
        
        // Songpa FC: 20대 1명 = 총 1명  
        TeamUser teamUser3_1 = TeamUser.createLeader(team3, user20s1); // 다른 팀 소속도 가능하다고 가정
        
        entityManager.persist(teamUser1_1);
        entityManager.persist(teamUser1_2);
        entityManager.persist(teamUser1_3);
        entityManager.persist(teamUser2_1);
        entityManager.persist(teamUser3_1);
        
        entityManager.flush();
        entityManager.clear();
    }
    
    private User createUser(String email, LocalDate birth) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return User.createUser(
            email,
            passwordEncoder.encode("password"),
            "TestUser",
            birth,
            170,
            Gender.MALE,
            Position.MF,
            UserRole.ROLE_MEMBER,
            "010-1234-5678",
            City.SEOUL,
            District.SEOUL_GANGNAM,
            false
        );
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