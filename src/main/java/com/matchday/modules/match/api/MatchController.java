package com.matchday.modules.match.api;

import com.matchday.common.entity.BaseResponse;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.api.spec.MatchControllerDocs;
import com.matchday.modules.match.api.dto.dto.request.MatchCreateRequest;
import com.matchday.modules.match.api.dto.dto.response.MatchListResponse;
import com.matchday.modules.match.api.dto.dto.response.MatchResponse;
import com.matchday.modules.match.application.MatchService;
import com.matchday.security.filter.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController implements MatchControllerDocs {
    
    private final MatchService matchService;

    // 매치 생성
    @PostMapping
    public BaseResponse<Long> createMatch(
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
            @Valid @RequestBody MatchCreateRequest request) {
        
        Long matchId = matchService.createMatch(userPrincipal.getUserId(), request);
        return BaseResponse.onSuccess(matchId, ResponseCode.OK);
    }

    // 매치 상세 페이지
    @GetMapping("/{matchId}")
    public BaseResponse<MatchResponse> getMatchDetails(@PathVariable Long matchId) {
        MatchResponse response = matchService.getMatchDetails(matchId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    // 가능한 매치 리스트 불러오기 TODO: pagination
    @GetMapping
    public BaseResponse<List<MatchListResponse>> getAvailableMatches() {
        List<MatchListResponse> response = matchService.getAvailableMatches();
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    // 내 모든 팀의 모든 매치 목록(response: 날짜, matchId, 페이지네이션: month)

    // 해당 팀이 등록한 모든 매치(response: 날짜, 시각, 설명, 장소, sportsType, fee, Size, district, 각 매치마다 받은 신청 요청도 같이 반환(팀장/운영진일 경우), 페이지네이션: createdDate)

    // 매치 취소
    @DeleteMapping("/{matchId}")
    public BaseResponse<String> cancelMatch(
            @PathVariable Long matchId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        matchService.cancelMatch(matchId, userPrincipal.getUserId());
        return BaseResponse.onSuccess("매치가 삭제되었습니다.", ResponseCode.OK);
    }
}