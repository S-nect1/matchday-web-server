package com.matchday.modules.team.controller;

import com.matchday.common.entity.BaseResponse;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.team.controller.spec.TeamUserControllerDocs;
import com.matchday.modules.team.domain.enums.TeamRole;
import com.matchday.modules.team.dto.request.TeamJoinRequest;
import com.matchday.modules.team.dto.response.TeamResponse;
import com.matchday.modules.team.dto.response.TeamUserResponse;
import com.matchday.modules.team.service.TeamUserService;
import com.matchday.security.filter.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamUserController implements TeamUserControllerDocs {
    
    private final TeamUserService teamUserService;

    @PostMapping("/join")
    public BaseResponse<TeamUserResponse> joinTeam(
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
            @Valid @RequestBody TeamJoinRequest request) {
        
        TeamUserResponse response = teamUserService.joinTeam(userPrincipal.getUserId(), request);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    @DeleteMapping("/{teamId}/members")
    public BaseResponse<String> leaveTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        teamUserService.leaveTeam(userPrincipal.getUserId(), teamId);
        return BaseResponse.onSuccess("팀 탈퇴가 완료되었습니다.", ResponseCode.OK);
    }

    @GetMapping("/{teamId}/members")
    public BaseResponse<List<TeamUserResponse>> getTeamMembers(@PathVariable Long teamId) {
        List<TeamUserResponse> response = teamUserService.getTeamMembers(teamId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    @GetMapping("/my-teams")
    public BaseResponse<List<TeamResponse>> getMyTeams(
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        List<TeamResponse> response = teamUserService.getUserTeams(userPrincipal.getUserId());
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    @PatchMapping("/{teamId}/members/{targetUserId}/role")
    public BaseResponse<TeamUserResponse> updateMemberRole(
            @PathVariable Long teamId,
            @PathVariable Long targetUserId,
            @RequestParam TeamRole role,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        TeamUserResponse response = teamUserService.updateMemberRole(
            teamId, targetUserId, role, userPrincipal.getUserId());
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    @DeleteMapping("/{teamId}/members/{targetUserId}")
    public BaseResponse<String> kickMember(
            @PathVariable Long teamId,
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    ) {
        teamUserService.kickMember(userPrincipal.getUserId(), targetUserId, teamId);
        return BaseResponse.onSuccess("회원이 퇴출되었습니다.", ResponseCode.OK);
    }
}