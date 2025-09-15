package com.matchday.modules.match.infrastructure;

import com.matchday.modules.match.api.dto.dto.projection.MatchListProjection;
import com.matchday.modules.match.api.dto.dto.projection.MatchListProjectionImpl;
import com.matchday.modules.match.api.dto.dto.request.MatchSearchRequest;
import com.matchday.modules.match.domain.QMatch;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.team.domain.QTeam;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MatchQueryRepositoryImpl implements MatchQueryRepository {
    
    private final JPAQueryFactory queryFactory;

    private final QMatch match = QMatch.match;
    private final QTeam homeTeam = QTeam.team;
    
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
                        match.createdDate
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
            case "createdAt" -> isAsc ? match.createdDate.asc() : match.createdDate.desc();
            case "date" -> isAsc ? match.date.asc() : match.date.desc();
            case "startTime" -> isAsc ? match.startTime.asc() : match.startTime.desc();
            case "fee" -> isAsc ? match.fee.asc() : match.fee.desc();
            case "homeTeamName" -> isAsc ? homeTeam.name.asc() : homeTeam.name.desc();
            default -> null;
        };
    }
}