package com.matchday.modules.match.api;

import com.matchday.common.dto.response.PagedResponse;
import com.matchday.common.entity.BaseResponse;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.api.spec.MatchControllerDocs;
import com.matchday.modules.match.api.dto.request.MatchCreateRequest;
import com.matchday.modules.match.api.dto.request.MatchSearchRequest;
import com.matchday.modules.match.api.dto.request.MatchUpdateRequest;
import com.matchday.modules.match.api.dto.request.MatchResultRequest;
import com.matchday.modules.match.api.dto.response.MatchListResponse;
import com.matchday.modules.match.api.dto.response.MatchResponse;
import com.matchday.modules.match.api.dto.response.MonthlyMatchResponse;
import com.matchday.modules.match.application.MatchService;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.SportsType;
import com.matchday.security.filter.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

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

    // 가능한 모든 매치 리스트
    @GetMapping
    public BaseResponse<PagedResponse<MatchListResponse>> getAvailableMatches(
            @RequestParam(required = false) SportsType sportsType,
            @RequestParam(required = false) MatchSize matchSize,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        MatchSearchRequest searchRequest = new MatchSearchRequest();
        
        searchRequest.setSportsType(sportsType);
        searchRequest.setMatchSize(matchSize);
        searchRequest.setStartDate(startDate);
        searchRequest.setEndDate(endDate);
        searchRequest.setKeyword(StringUtils.hasText(keyword) ? keyword.trim() : null);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSort(sort);
        searchRequest.setDirection(direction);

        PagedResponse<MatchListResponse> response = matchService.getAvailableMatches(searchRequest);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    // 내가 가입한 모든 팀의 확정된 매치/신청 목록 조회 (캘린더용)
    @GetMapping("/my")
    public BaseResponse<MonthlyMatchResponse> getAllMyMatches(
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        // 현재 년/월 기본
        YearMonth targetMonth = (year != null && month != null) ? 
                YearMonth.of(year, month) : YearMonth.now();
        
        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();
        
        MonthlyMatchResponse response = matchService.getMonthlyMatches(
                userPrincipal.getUserId(), startDate, endDate);
        
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    // 매치 수정
    @PatchMapping("/{matchId}")
    public BaseResponse<String> updateMatch(
            @PathVariable Long matchId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
            @Valid @RequestBody MatchUpdateRequest request) {
        
        matchService.updateMatch(matchId, userPrincipal.getUserId(), request);
        return BaseResponse.onSuccess("매치가 수정되었습니다.", ResponseCode.OK);
    }

    // 매치 결과 기록 (48시간 이내에만 가능)
    @PatchMapping("/{matchId}/result")
    public BaseResponse<String> recordMatchResult(
            @PathVariable Long matchId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
            @Valid @RequestBody MatchResultRequest request) {
        
        matchService.recordMatchResult(matchId, userPrincipal.getUserId(), request);
        return BaseResponse.onSuccess("매치 결과가 기록되었습니다.", ResponseCode.OK);
    }

    // 매치 취소
    @DeleteMapping("/{matchId}")
    public BaseResponse<String> cancelMatch(
            @PathVariable Long matchId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        matchService.cancelMatch(matchId, userPrincipal.getUserId());
        return BaseResponse.onSuccess("매치가 삭제되었습니다.", ResponseCode.OK);
    }
}