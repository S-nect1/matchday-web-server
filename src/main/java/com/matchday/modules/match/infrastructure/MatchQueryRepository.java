package com.matchday.modules.match.infrastructure;

import com.matchday.modules.match.api.dto.dto.projection.MatchListProjection;
import com.matchday.modules.match.api.dto.dto.request.MatchSearchRequest;
import com.matchday.modules.match.domain.enums.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MatchQueryRepository {

    Page<MatchListProjection> findAvailableMatches(
            MatchStatus status,
            MatchSearchRequest searchRequest,
            Pageable pageable
    );
}