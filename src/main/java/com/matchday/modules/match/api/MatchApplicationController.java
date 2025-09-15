package com.matchday.modules.match.api;

import com.matchday.common.entity.BaseResponse;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.api.spec.MatchApplicationControllerDocs;
import com.matchday.modules.match.api.dto.dto.request.MatchApplicationRequest;
import com.matchday.modules.match.api.dto.dto.response.MatchApplicationResponse;
import com.matchday.modules.match.application.MatchApplicationService;
import com.matchday.security.filter.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchApplicationController implements MatchApplicationControllerDocs {
    
    private final MatchApplicationService matchApplicationService;

    // 매치에 신청 (POST /matches/{matchId}/applications)
    @PostMapping("/{matchId}/applications")
    public BaseResponse<Long> applyToMatch(
            @PathVariable Long matchId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
            @Valid @RequestBody MatchApplicationRequest request) {
        
        Long applicationId = matchApplicationService.applyToMatch(userPrincipal.getUserId(), matchId, request);
        return BaseResponse.onSuccess(applicationId, ResponseCode.OK);
    }

    // 특정 매치에 받은 신청 목록 조회 (GET /matches/{matchId}/applications)
    @GetMapping("/{matchId}/applications")
    public BaseResponse<List<MatchApplicationResponse>> getMatchApplications(
            @PathVariable Long matchId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        List<MatchApplicationResponse> response = matchApplicationService.getMatchApplications(userPrincipal.getUserId(), matchId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    // 신청 수락
    @PatchMapping("/applications/{applicationId}/accept")
    public BaseResponse<String> acceptApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        matchApplicationService.acceptApplication(userPrincipal.getUserId(), applicationId);
        return BaseResponse.onSuccess("매치 신청이 수락되었습니다.", ResponseCode.OK);
    }

    // 신청 거절
    @PatchMapping("/applications/{applicationId}/reject")
    public BaseResponse<String> rejectApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        matchApplicationService.rejectApplication(userPrincipal.getUserId(), applicationId);
        return BaseResponse.onSuccess("매치 신청이 거절되었습니다.", ResponseCode.OK);
    }

    // 신청 취소
    @PatchMapping("/applications/{applicationId}/cancel")
    public BaseResponse<String> cancelApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        matchApplicationService.cancelApplication(userPrincipal.getUserId(), applicationId);
        return BaseResponse.onSuccess("매치 신청이 취소되었습니다.", ResponseCode.OK);
    }
}