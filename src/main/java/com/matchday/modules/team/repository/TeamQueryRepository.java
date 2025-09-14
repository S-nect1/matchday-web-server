package com.matchday.modules.team.repository;

import com.matchday.modules.team.dto.request.TeamSearchRequest;
import com.matchday.modules.team.dto.response.TeamListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamQueryRepository {
    
    /**
     * 동적 조건을 사용한 팀 목록 조회
     * @param searchRequest 검색 조건
     * @param pageable 페이지네이션 정보
     * @return 페이지네이션된 팀 목록
     */
    Page<TeamListResponse> findTeamsByConditions(TeamSearchRequest searchRequest, Pageable pageable);
}