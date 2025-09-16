package com.matchday.modules.team.api;

import com.matchday.common.dto.response.PagedResponse;
import com.matchday.common.entity.BaseResponse;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.api.dto.response.MatchApplicationResponse;
import com.matchday.modules.match.api.dto.response.MatchListResponse;
import com.matchday.modules.match.api.dto.response.TeamConfirmedMatchResponse;
import com.matchday.modules.match.application.MatchApplicationService;
import com.matchday.modules.match.application.MatchService;
import com.matchday.modules.team.api.spec.TeamMatchControllerDocs;
import com.matchday.security.filter.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamMatchController implements TeamMatchControllerDocs {
    
    private final MatchService matchService;
    private final MatchApplicationService matchApplicationService;

    // 팀이 등록한 매치 목록 조회 TODO: projection
    @GetMapping("/{teamId}/matches")
    public BaseResponse<List<MatchListResponse>> getTeamMatches(
            @PathVariable Long teamId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        List<MatchListResponse> response = matchService.getTeamMatches(userPrincipal.getUserId(), teamId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    // 팀이 신청한 매치 목록 조회 TODO: projection
    @GetMapping("/{teamId}/applications")
    public BaseResponse<List<MatchApplicationResponse>> getTeamApplications(
            @PathVariable Long teamId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        List<MatchApplicationResponse> response = matchApplicationService.getTeamApplications(userPrincipal.getUserId(), teamId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    // 팀이 받은 신청 목록 조회 TODO: projection
    @GetMapping("/{teamId}/received-applications")
    public BaseResponse<List<MatchApplicationResponse>> getReceivedApplications(
            @PathVariable Long teamId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        List<MatchApplicationResponse> response = matchApplicationService.getReceivedApplications(userPrincipal.getUserId(), teamId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    // 팀의 확정된 매치 목록 조회
    @GetMapping("/{teamId}/confirmed-matches")
    public BaseResponse<PagedResponse<TeamConfirmedMatchResponse>> getTeamConfirmedMatches(
            @PathVariable Long teamId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        PagedResponse<TeamConfirmedMatchResponse> response = 
                matchService.getTeamConfirmedMatches(
                        userPrincipal.getUserId(), teamId, page, size, sort, direction);
        
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }
}