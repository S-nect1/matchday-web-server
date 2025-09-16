package com.matchday.modules.team.infrastructure;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.team.domain.QTeam;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamType;
import com.matchday.modules.team.domain.QTeamUser;
import com.matchday.modules.team.api.dto.dto.request.TeamSearchRequest;
import com.matchday.modules.team.api.dto.dto.response.TeamListResponse;
import com.matchday.modules.user.domain.QUser;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamQueryRepositoryImpl implements TeamQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QTeam team = QTeam.team;
    private final QTeamUser teamUser = QTeamUser.teamUser;
    private final QUser user = QUser.user;

    @Override
    @Transactional(readOnly = true)
    public Page<TeamListResponse> findTeamsByConditions(TeamSearchRequest req, Pageable pageable) {
        BooleanExpression city     = cityEq(req.getCity());
        BooleanExpression district = districtEq(req.getDistrict());
        BooleanExpression type     = typeEq(req.getType());
        BooleanExpression gender   = genderEq(req.getGender());
        BooleanExpression keyword  = nameContains(req.getKeyword());

        // pageable의 Sort가 있으면 우선 적용, 없으면 req의 sort/direction 사용
        OrderSpecifier<?>[] orderSpecifiers = orderSpecifiersFrom(req, pageable);

        if (req.getAgeGroup() != null) {
            // 연령대 필터링이 있는 경우: 서브쿼리로 평균 나이 기준 팀 ID 먼저 조회
            return findTeamsByConditionsWithAgeGroup(req, pageable, city, district, type, gender, keyword, orderSpecifiers);
        }

        // 연령대 필터링이 없는 경우: 기본 쿼리
        List<TeamListResponse> content = queryFactory
                .select(Projections.fields(TeamListResponse.class,
                        team.id.as("id"),
                        team.name.as("name"),
                        team.description.as("description"),
                        team.type.as("type"),
                        team.city.as("city"),
                        team.district.as("district"),
                        team.gender.as("gender"),
                        team.uniformColorHex.as("uniformColorHex"),
                        team.memberLimit.as("memberLimit"),
                        team.hasBall.as("hasBall"),
                        team.profileImageUrl.as("profileImageUrl"),
                        team.createdAt.as("createdAt"),
                        teamUser.countDistinct().intValue().as("memberCount")
                ))
                .from(team)
                .leftJoin(teamUser).on(teamUser.team.eq(team))
                .where(city, district, type, gender, keyword)
                .groupBy(team.id, team.name, team.description, team.type, team.city, 
                         team.district, team.gender, team.uniformColorHex, team.memberLimit, 
                         team.hasBall, team.profileImageUrl, team.createdAt)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(team.countDistinct())
                .from(team)
                .where(city, district, type, gender, keyword);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression cityEq(City city) {
        return city != null ? team.city.eq(city) : null;
    }

    private BooleanExpression districtEq(District district) {
        return district != null ? team.district.eq(district) : null;
    }

    private BooleanExpression typeEq(TeamType type) {
        return type != null ? team.type.eq(type) : null;
    }

    private BooleanExpression genderEq(GroupGender gender) {
        return gender != null ? team.gender.eq(gender) : null;
    }

    private BooleanExpression nameContains(String keyword) {
        return StringUtils.hasText(keyword) ? team.name.containsIgnoreCase(keyword) : null;
    }

    private OrderSpecifier<?>[] orderSpecifiersFrom(TeamSearchRequest req, Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        // 1) Pageable의 Sort 우선 적용(다중 정렬 지원)
        if (pageable != null && pageable.getSort().isSorted()) {
            for (Sort.Order o : pageable.getSort()) {
                boolean asc = o.getDirection().isAscending();
                OrderSpecifier<?> spec = toAllowedOrderSpecifier(o.getProperty(), asc);

                if (spec != null) orders.add(spec);
            }
        }

        // 2) 요청 파라미터 정렬(폴백)
        if (orders.isEmpty()) {
            boolean asc = "asc".equalsIgnoreCase(req.getDirection());
            OrderSpecifier<?> spec = toAllowedOrderSpecifier(req.getSort(), asc);

            if (spec != null) {
                orders.add(spec);
            } else {
                orders.add(team.createdAt.desc());
            }
        }

        return orders.toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> toAllowedOrderSpecifier(String key, boolean asc) {
        if (key == null) return null;

        return switch (key) {
            case "name" -> asc ? team.name.asc() : team.name.desc();
            case "createdAt" -> asc ? team.createdAt.asc() : team.createdAt.desc();
            default -> null;
        };
    }

    private Page<TeamListResponse> findTeamsByConditionsWithAgeGroup(
            TeamSearchRequest req, Pageable pageable, 
            BooleanExpression city, BooleanExpression district, BooleanExpression type, 
            BooleanExpression gender, BooleanExpression keyword, OrderSpecifier<?>[] orderSpecifiers) {
        
        // 평균 나이가 해당 연령대에 속하는 팀 ID 조회
        int minAge = req.getAgeGroup();
        int maxAge = req.getAgeGroup() + 9;
        
        List<Long> teamIds = queryFactory
                .select(teamUser.team.id)
                .from(teamUser)
                .join(user).on(teamUser.user.eq(user))
                .join(team).on(teamUser.team.eq(team))
                .where(city, district, type, gender, keyword)
                .groupBy(teamUser.team.id)
                .having(Expressions.numberTemplate(Double.class, 
                        "AVG(YEAR(CURDATE()) - YEAR({0}))", user.birth)
                        .between(minAge, maxAge))
                .fetch();

        if (teamIds.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // 조건에 맞는 팀들의 상세 정보 조회
        List<TeamListResponse> content = queryFactory
                .select(Projections.fields(TeamListResponse.class,
                        team.id.as("id"),
                        team.name.as("name"),
                        team.description.as("description"),
                        team.type.as("type"),
                        team.city.as("city"),
                        team.district.as("district"),
                        team.gender.as("gender"),
                        team.uniformColorHex.as("uniformColorHex"),
                        team.memberLimit.as("memberLimit"),
                        team.hasBall.as("hasBall"),
                        team.profileImageUrl.as("profileImageUrl"),
                        team.createdAt.as("createdAt"),
                        teamUser.countDistinct().intValue().as("memberCount")
                ))
                .from(team)
                .leftJoin(teamUser).on(teamUser.team.eq(team))
                .where(team.id.in(teamIds))
                .groupBy(team.id, team.name, team.description, team.type, team.city, 
                         team.district, team.gender, team.uniformColorHex, team.memberLimit, 
                         team.hasBall, team.profileImageUrl, team.createdAt)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 3단계: 카운트 쿼리 (총 개수)
        long total = teamIds.size();
        
        return new PageImpl<>(content, pageable, total);
    }
}