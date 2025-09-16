package com.matchday.modules.match.infrastructure;

import com.matchday.modules.match.api.dto.projection.MatchListProjection;
import com.matchday.modules.match.api.dto.projection.MatchListProjectionImpl;
import com.matchday.modules.match.api.dto.projection.TeamConfirmedMatchProjection;
import com.matchday.modules.match.api.dto.projection.TeamConfirmedMatchProjectionImpl;
import com.matchday.modules.match.api.dto.request.MatchSearchRequest;
import com.matchday.modules.match.api.dto.response.MonthlyMatchResponse;
import com.matchday.modules.match.domain.QMatch;
import com.matchday.modules.match.domain.QMatchApplication;
import com.matchday.modules.match.domain.enums.MatchApplicationStatus;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.team.domain.QTeam;
import com.matchday.modules.team.domain.QTeamUser;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MatchQueryRepositoryImpl implements MatchQueryRepository {
    
    private final JPAQueryFactory queryFactory;

    private final QMatch match = QMatch.match;
    private final QTeam homeTeam = QTeam.team;
    private final QTeam awayTeam = new QTeam("awayTeam");
    private final QTeamUser teamUser = QTeamUser.teamUser;
    private final QMatchApplication matchApplication = QMatchApplication.matchApplication;
    
    @Override
    public Page<MatchListProjection> findAvailableMatches(
            MatchStatus status,
            MatchSearchRequest searchRequest,
            Pageable pageable) {
        
        BooleanExpression statusCondition = statusEq(status);
        BooleanExpression sportsType = sportsTypeEq(searchRequest.getSportsType());
        BooleanExpression matchSize = matchSizeEq(searchRequest.getMatchSize());
        BooleanExpression dateRange = dateRangeFilter(searchRequest.getStartDate(), searchRequest.getEndDate());
        BooleanExpression keyword = keywordContains(searchRequest.getKeyword());
        
        JPAQuery<MatchListProjectionImpl> query = queryFactory
                .select(Projections.constructor(MatchListProjectionImpl.class,
                        match.id,
                        homeTeam.name,
                        match.city,
                        match.district,
                        match.placeName,
                        match.date,
                        match.startTime,
                        match.endTime,
                        match.fee,
                        match.matchSize,
                        match.sportsType,
                        match.createdAt
                ))
                .from(match)
                .join(match.homeTeam, homeTeam)
                .where(statusCondition, sportsType, matchSize, dateRange, keyword)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        
        OrderSpecifier<?> orderSpecifier = createOrderSpecifier(searchRequest, match, homeTeam);
        if (orderSpecifier != null) {
            query.orderBy(orderSpecifier);
        } else {
            query.orderBy(match.date.asc(), match.startTime.asc());
        }
        
        List<MatchListProjectionImpl> results = query.fetch();
        List<MatchListProjection> content = results.stream()
                .map(MatchListProjection.class::cast)
                .toList();
        
        Long totalCount = queryFactory
                .select(match.count())
                .from(match)
                .join(match.homeTeam, homeTeam)
                .where(statusCondition, sportsType, matchSize, dateRange, keyword)
                .fetchOne();
        
        return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0);
    }

//    // 내 모든 팀의 확정된 매치
//    @Override
//    public List<MyMatchResponse> findMyMatches(Long userId, LocalDate startDate, LocalDate endDate) {
//        // 홈팀으로 등록한 매치들
//        List<MyMatchResponse> homeMatches = queryFactory
//                .select(Projections.constructor(MyMatchResponse.class,
//                        match.id,
//                        homeTeam.name,
//                        awayTeam.name.coalesce("미정"),
//                        match.city,
//                        match.district,
//                        match.placeName,
//                        match.date,
//                        match.startTime,
//                        match.endTime,
//                        match.fee,
//                        match.matchSize,
//                        match.sportsType,
//                        match.status,
//                        new CaseBuilder()
//                                .when(match.status.eq(MatchStatus.CONFIRMED)
//                                        .and(matchApplication.applicantTeam.eq(teamUser.team)))
//                                .then("AWAY")
//                                .otherwise("HOME"),
//                        match.homeScore,
//                        match.awayScore
//                ))
//                .from(match)
//                .join(match.homeTeam, homeTeam)
//                .join(teamUser).on(teamUser.team.eq(homeTeam))
//                .leftJoin(matchApplication).on(matchApplication.match.eq(match)
//                        .and(matchApplication.status.eq(MatchApplicationStatus.ACCEPTED)))
//                .leftJoin(matchApplication.applicantTeam, awayTeam)
//                .where(
//                        teamUser.user.id.eq(userId),
//                        dateRangeFilter(startDate, endDate),
//                        match.status.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED, MatchStatus.PENDING)
//                )
//                .orderBy(match.date.asc(), match.startTime.asc())
//                .fetch();
//
//        // 어웨이팀으로 신청한 매치들 (수락된 매치만)
//        List<MyMatchResponse> awayMatches = queryFactory
//                .select(Projections.constructor(MyMatchResponse.class,
//                        match.id,
//                        homeTeam.name,
//                        awayTeam.name,
//                        match.city,
//                        match.district,
//                        match.placeName,
//                        match.date,
//                        match.startTime,
//                        match.endTime,
//                        match.fee,
//                        match.matchSize,
//                        match.sportsType,
//                        match.status,
//                        new CaseBuilder().when(homeTeam.eq(teamUser.team)).then("HOME").otherwise("AWAY"),
//                        match.homeScore,
//                        match.awayScore
//                ))
//                .from(matchApplication)
//                .join(matchApplication.match, match)
//                .join(match.homeTeam, homeTeam)
//                .join(matchApplication.applicantTeam, awayTeam)
//                .join(teamUser).on(teamUser.team.eq(awayTeam))
//                .where(
//                        teamUser.user.id.eq(userId),
//                        matchApplication.status.eq(MatchApplicationStatus.ACCEPTED),
//                        dateRangeFilter(startDate, endDate),
//                        match.status.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED)
//                )
//                .orderBy(match.date.asc(), match.startTime.asc())
//                .fetch();
//
//        // 두 리스트를 합치고 날짜순으로 정렬
//        homeMatches.addAll(awayMatches);
//        return homeMatches.stream()
//                .distinct()
//                .sorted((m1, m2) -> {
//                    int dateCompare = m1.getDate().compareTo(m2.getDate());
//                    return dateCompare != 0 ? dateCompare : m1.getStartTime().compareTo(m2.getStartTime());
//                })
//                .toList();
//    }

    // 특정 팀의 모든 확정된 매치
    @Override
    public Page<TeamConfirmedMatchProjection> findTeamConfirmedMatches(Long teamId, Pageable pageable) {
        // 홈팀으로 확정된 매치들
        JPAQuery<TeamConfirmedMatchProjectionImpl> homeMatchQuery = queryFactory
                .select(Projections.constructor(TeamConfirmedMatchProjectionImpl.class,
                        match.id,
                        awayTeam.name.coalesce("미정"),
                        new CaseBuilder().when(homeTeam.id.eq(teamId)).then("HOME").otherwise("AWAY"),
                        match.city,
                        match.district,
                        match.placeName,
                        match.date,
                        match.startTime,
                        match.endTime,
                        match.fee,
                        match.matchSize,
                        match.sportsType,
                        match.status,
                        match.homeScore,
                        match.awayScore
                ))
                .from(match)
                .join(match.homeTeam, homeTeam)
                .leftJoin(matchApplication).on(matchApplication.match.eq(match)
                        .and(matchApplication.status.eq(MatchApplicationStatus.ACCEPTED)))
                .leftJoin(matchApplication.applicantTeam, awayTeam)
                .where(
                        homeTeam.id.eq(teamId),
                        match.status.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED)
                );

        // 어웨이팀으로 확정된 매치들 (수락된 신청만)
        JPAQuery<TeamConfirmedMatchProjectionImpl> awayMatchQuery = queryFactory
                .select(Projections.constructor(TeamConfirmedMatchProjectionImpl.class,
                        match.id,
                        homeTeam.name,
                        new CaseBuilder().when(awayTeam.id.eq(teamId)).then("AWAY").otherwise("HOME"),
                        match.city,
                        match.district,
                        match.placeName,
                        match.date,
                        match.startTime,
                        match.endTime,
                        match.fee,
                        match.matchSize,
                        match.sportsType,
                        match.status,
                        match.homeScore,
                        match.awayScore
                ))
                .from(matchApplication)
                .join(matchApplication.match, match)
                .join(match.homeTeam, homeTeam)
                .join(matchApplication.applicantTeam, awayTeam)
                .where(
                        awayTeam.id.eq(teamId),
                        matchApplication.status.eq(MatchApplicationStatus.ACCEPTED),
                        match.status.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED)
                );

        // 홈팀 매치와 어웨이팀 매치를 각각 조회 후 합치기
        List<TeamConfirmedMatchProjectionImpl> homeResults = homeMatchQuery.fetch();
        List<TeamConfirmedMatchProjectionImpl> awayResults = awayMatchQuery.fetch();
        
        // 결과를 합치고 정렬
        List<TeamConfirmedMatchProjectionImpl> allResults = new java.util.ArrayList<>();
        allResults.addAll(homeResults);
        allResults.addAll(awayResults);
        
        allResults.sort((m1, m2) -> {
            int dateCompare = m2.getDate().compareTo(m1.getDate());
            return dateCompare != 0 ? dateCompare : m2.getStartTime().compareTo(m1.getStartTime());
        });
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allResults.size());
        List<TeamConfirmedMatchProjectionImpl> results = allResults.subList(start, end);

        // 총 개수 조회 (별도 쿼리)
        Long homeMatchCount = queryFactory
                .select(match.count())
                .from(match)
                .join(match.homeTeam, homeTeam)
                .where(
                        homeTeam.id.eq(teamId),
                        match.status.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED)
                )
                .fetchOne();

        Long awayMatchCount = queryFactory
                .select(matchApplication.count())
                .from(matchApplication)
                .join(matchApplication.match, match)
                .join(matchApplication.applicantTeam, awayTeam)
                .where(
                        awayTeam.id.eq(teamId),
                        matchApplication.status.eq(MatchApplicationStatus.ACCEPTED),
                        match.status.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED)
                )
                .fetchOne();

        Long totalCount = (homeMatchCount != null ? homeMatchCount : 0) + 
                         (awayMatchCount != null ? awayMatchCount : 0);

        List<TeamConfirmedMatchProjection> content = results.stream()
                .map(TeamConfirmedMatchProjection.class::cast)
                .toList();

        return new PageImpl<>(content, pageable, totalCount);
    }

    // 월별 확정된 모든 매치 조회 (성능 최적화) - 일자별 매치 ID만 조회
    @Override
    public MonthlyMatchResponse findMonthlyMatches(Long userId, LocalDate startDate, LocalDate endDate) {
        // 홈팀으로 등록한 매치의 일자별 ID 조회
        List<Tuple> homeMatchDates = queryFactory
                .select(match.id, match.date.dayOfMonth())
                .from(match)
                .join(match.homeTeam, homeTeam)
                .join(teamUser).on(teamUser.team.eq(homeTeam))
                .leftJoin(matchApplication).on(matchApplication.match.eq(match)
                        .and(matchApplication.status.eq(MatchApplicationStatus.ACCEPTED)))
                .where(
                        teamUser.user.id.eq(userId),
                        dateRangeFilter(startDate, endDate),
                        match.status.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED, MatchStatus.PENDING)
                )
                .fetch();

        // 어웨이팀으로 신청한 매치의 일자별 ID 조회 (수락된 매치만)
        List<Tuple> awayMatchDates = queryFactory
                .select(match.id, match.date.dayOfMonth())
                .from(matchApplication)
                .join(matchApplication.match, match)
                .join(matchApplication.applicantTeam, awayTeam)
                .join(teamUser).on(teamUser.team.eq(awayTeam))
                .where(
                        teamUser.user.id.eq(userId),
                        matchApplication.status.eq(MatchApplicationStatus.ACCEPTED),
                        dateRangeFilter(startDate, endDate),
                        match.status.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED)
                )
                .fetch();

        // 모든 매치 데이터를 합쳐서 일자별로 그룹핑
        Map<Integer, List<Long>> dailyMatches = homeMatchDates.stream()
                .collect(Collectors.groupingBy(
                        result -> result.get(match.date.dayOfMonth()), // 일자 (dayOfMonth)
                        Collectors.mapping(
                                result -> result.get(match.id), // 매치 ID
                                Collectors.toList()
                        )
                ));

        // 어웨이 매치 데이터 추가
        awayMatchDates.forEach(result -> {
            Integer day = result.get(match.date.dayOfMonth());
            Long matchId = result.get(match.id);
            dailyMatches.computeIfAbsent(day, k -> new java.util.ArrayList<>()).add(matchId);
        });

        Map<Integer, List<Long>> processedDailyMatches = dailyMatches.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().distinct().sorted().collect(Collectors.toList())
                ));

        return MonthlyMatchResponse.of(processedDailyMatches);
    }

    // 조건 메서드들
    private BooleanExpression statusEq(MatchStatus status) {
        return status != null ? match.status.eq(status) : null;
    }
    
    private BooleanExpression sportsTypeEq(com.matchday.modules.match.domain.enums.SportsType sportsType) {
        return sportsType != null ? match.sportsType.eq(sportsType) : null;
    }
    
    private BooleanExpression matchSizeEq(com.matchday.modules.match.domain.enums.MatchSize matchSize) {
        return matchSize != null ? match.matchSize.eq(matchSize) : null;
    }
    
    private BooleanExpression dateRangeFilter(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        BooleanExpression condition = null;
        
        if (startDate != null) {
            condition = match.date.goe(startDate);
        }
        
        if (endDate != null) {
            condition = condition != null ? 
                condition.and(match.date.loe(endDate)) : 
                match.date.loe(endDate);
        }
        
        return condition;
    }
    
    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        
        String searchKeyword = "%" + keyword.toLowerCase() + "%";
        return homeTeam.name.toLowerCase().like(searchKeyword)
                .or(match.placeName.toLowerCase().like(searchKeyword));
    }

    // 페이지네이션 정렬 조건
    private OrderSpecifier<?> createOrderSpecifier(MatchSearchRequest searchRequest, QMatch match, QTeam homeTeam) {
        String sort = searchRequest.getSort();
        String direction = searchRequest.getDirection();
        boolean isAsc = "asc".equalsIgnoreCase(direction);
        
        return switch (sort) {
            case "createdAt" -> isAsc ? match.createdAt.asc() : match.createdAt.desc();
            case "date" -> isAsc ? match.date.asc() : match.date.desc();
            case "startTime" -> isAsc ? match.startTime.asc() : match.startTime.desc();
            case "fee" -> isAsc ? match.fee.asc() : match.fee.desc();
            case "homeTeamName" -> isAsc ? homeTeam.name.asc() : homeTeam.name.desc();
            default -> null;
        };
    }
}