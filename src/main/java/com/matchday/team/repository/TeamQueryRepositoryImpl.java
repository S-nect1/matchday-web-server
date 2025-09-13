package com.matchday.team.repository;

import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.QTeam;
import com.matchday.team.domain.Team;
import com.matchday.team.domain.enums.GroupGender;
import com.matchday.team.domain.enums.TeamType;
import com.matchday.team.dto.request.TeamSearchRequest;
import com.matchday.team.dto.response.TeamListResponse;
import com.matchday.user.domain.enums.Gender;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
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

        // 내용 쿼리
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
                        team.createdDate.as("createdAt")
                ))
                .from(team)
                .where(city, district, type, gender, keyword)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(team.count())
                .from(team)
                .where(city, district, type, gender, keyword);

        // 5) PageableExecutionUtils: 마지막 페이지에서 불필요한 카운트 호출을 지연/스킵
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
                orders.add(team.createdDate.desc());
            }
        }

        return orders.toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> toAllowedOrderSpecifier(String key, boolean asc) {
        if (key == null) return null;

        return switch (key) {
            case "name" -> asc ? team.name.asc() : team.name.desc();
            case "createdAt" -> asc ? team.createdDate.asc() : team.createdDate.desc();
            default -> null;
        };
    }
}