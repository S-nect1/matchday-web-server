package com.matchday.modules.match.infrastructure;

import com.matchday.modules.match.api.dto.projection.MatchListProjection;
import com.matchday.modules.match.api.dto.projection.TeamConfirmedMatchProjection;
import com.matchday.modules.match.api.dto.request.MatchSearchRequest;
import com.matchday.modules.match.api.dto.response.MonthlyMatchResponse;
import com.matchday.modules.match.domain.enums.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MatchQueryRepository {

    Page<MatchListProjection> findAvailableMatches(
            MatchStatus status,
            MatchSearchRequest searchRequest,
            Pageable pageable
    );

    // 특정 사용자가 속한 팀들의 모든 확정된 매치 조회 (월별 조회)
//    List<MyMatchResponse> findMyMatches(
//            Long userId,
//            LocalDate startDate,
//            LocalDate endDate
//    );
    
    // 특정 팀의 확정된 매치/신청 목록 조회
    Page<TeamConfirmedMatchProjection> findTeamConfirmedMatches(
            Long teamId,
            Pageable pageable
    );
    
    // 월별 확정된 매치 조회
    MonthlyMatchResponse findMonthlyMatches(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}