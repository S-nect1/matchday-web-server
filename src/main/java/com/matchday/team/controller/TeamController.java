package com.matchday.team.controller;

import com.matchday.global.entity.BaseResponse;
import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.team.dto.request.TeamCreateRequest;
import com.matchday.team.dto.request.TeamUpdateRequest;
import com.matchday.team.dto.response.TeamResponse;
import com.matchday.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 임시로 헤더에서 사용자 ID 받음
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {
    
    private final TeamService teamService;

    /** 팀 생성
     *
     * @param request 팀 생성 요청 DTO
     * @return 팀 상세 페이지로 리다이렉트
     */
    @PostMapping
    public BaseResponse<Long> createTeam(@Valid @RequestBody TeamCreateRequest request, @RequestHeader("User-Id") Long userId) {
        return BaseResponse.onSuccess(teamService.createTeam(userId, request), ResponseCode.OK);
    }

    /** 팀 상세 조회
     *
     * @param teamId 팀 식별자(기본키)
     * @return 팀 상세 내용
     */
    @GetMapping("/{teamId}")
    public BaseResponse<TeamResponse> getTeam(@PathVariable Long teamId) {
        TeamResponse response = TeamResponse.from(teamService.getTeam(teamId));

        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    /** 팀 정보 수정
     *
     * @param teamId 팀 식별자(기본키)
     * @param userId 유저 식별자(기본키)
     * @param request 수정 요청 DTO
     * @return 수정된 팀 정보
     */
    @PatchMapping("/{teamId}")
    public BaseResponse<TeamResponse> updateTeam(
            @PathVariable Long teamId,
            @RequestHeader("User-Id") Long userId,
            @Valid @RequestBody TeamUpdateRequest request) {
        
        TeamResponse response = teamService.updateTeam(teamId, request, userId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    /** 팀 삭제
     *
     * @param teamId 팀 식별자(기본키)
     * @param userId 유저 식별자(기본키)
     * @return 메인 페이지로 리다이렉트
     */
    @DeleteMapping("/{teamId}")
    public BaseResponse<String> deleteTeam(
            @PathVariable Long teamId,
            @RequestHeader("User-Id") Long userId) {
        
        teamService.deleteTeam(teamId, userId);
        return BaseResponse.onSuccess("팀이 삭제되었습니다.", ResponseCode.OK);
    }

    /** 팀 목록 (조건에 맞게)
     *
     */
//    @GetMapping
//    public BaseResponse<List<TeamResponse>> getTeams() {
//        return BaseResponse.onSuccess();
//    }
}