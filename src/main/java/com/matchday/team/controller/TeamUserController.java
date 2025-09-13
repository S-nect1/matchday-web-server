package com.matchday.team.controller;

import com.matchday.global.entity.BaseResponse;
import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.team.domain.enums.TeamRole;
import com.matchday.team.dto.request.TeamJoinRequest;
import com.matchday.team.dto.response.TeamResponse;
import com.matchday.team.dto.response.TeamUserResponse;
import com.matchday.team.service.TeamUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamUserController {
    
    private final TeamUserService teamUserService;

    /** 초대코드로 팀 가입
     *
     * @param userId 유저 식별자(기본키)
     * @param request 초대코드 (등번호 필요에 따라 추가)
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<TeamUserResponse> joinTeam(
            @RequestHeader("User-Id") Long userId,
            @Valid @RequestBody TeamJoinRequest request) {
        
        TeamUserResponse response = teamUserService.joinTeam(userId, request);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    /** 팀 탈퇴
     *
     * @param teamId 팀 식별자(기본키)
     * @param userId 유저 식별자(기본키)
     * @return
     */
    @DeleteMapping("/{teamId}/members")
    public BaseResponse<String> leaveTeam(
            @PathVariable Long teamId,
            @RequestHeader("User-Id") Long userId) {
        
        teamUserService.leaveTeam(userId, teamId);
        return BaseResponse.onSuccess("팀 탈퇴가 완료되었습니다.", ResponseCode.OK);
    }

    /** 팀 멤버 목록 조회
     *
     * @param teamId 팀 식별자(기본키)
     * @return
     */
    @GetMapping("/{teamId}/members")
    public BaseResponse<List<TeamUserResponse>> getTeamMembers(@PathVariable Long teamId) {
        List<TeamUserResponse> response = teamUserService.getTeamMembers(teamId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    /** 사용자가 가입한 팀 목록 조회
     *
     * @param userId 유저 식별자(기본키)
     * @return
     */
    @GetMapping("/my-teams")
    public BaseResponse<List<TeamResponse>> getMyTeams(
            @RequestHeader("User-Id") Long userId) {
        
        List<TeamResponse> response = teamUserService.getUserTeams(userId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    /** 멤버 역할 변경 (팀장만 가능)
     *
     * @param teamId 팀 식별자(기본키)
     * @param targetUserId 대상 유저 식별자(기본키)
     * @param role
     * @param requestUserId
     * @return
     */
    @PatchMapping("/{teamId}/members/{targetUserId}/role")
    public BaseResponse<TeamUserResponse> updateMemberRole(
            @PathVariable Long teamId,
            @PathVariable Long targetUserId,
            @RequestParam TeamRole role,
            @RequestHeader("User-Id") Long requestUserId) {
        
        TeamUserResponse response = teamUserService.updateMemberRole(
            teamId, targetUserId, role, requestUserId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }
}