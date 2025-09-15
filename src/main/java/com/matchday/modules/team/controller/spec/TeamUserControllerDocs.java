package com.matchday.modules.team.controller.spec;

import com.matchday.common.entity.BaseResponse;
import com.matchday.modules.team.domain.enums.TeamRole;
import com.matchday.modules.team.dto.request.TeamJoinRequest;
import com.matchday.modules.team.dto.response.TeamResponse;
import com.matchday.modules.team.dto.response.TeamUserResponse;
import com.matchday.security.filter.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "팀 멤버 관리", description = "팀 가입, 탈퇴, 멤버 관리 API")
public interface TeamUserControllerDocs {

    @Operation(
        summary = "초대코드로 팀 가입",
        description = "초대코드를 사용하여 팀에 가입합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "팀 가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 초대코드"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
        }
    )
    @PostMapping("/join")
    BaseResponse<TeamUserResponse> joinTeam(
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
        @Valid @RequestBody TeamJoinRequest request
    );

    @Operation(
        summary = "팀 탈퇴",
        description = "팀에서 탈퇴합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "팀 탈퇴 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
        }
    )
    @DeleteMapping("/{teamId}/members")
    BaseResponse<String> leaveTeam(
        @Parameter(description = "팀 ID", example = "1")
        @PathVariable Long teamId,
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );

    @Operation(
        summary = "팀 멤버 목록 조회",
        description = "특정 팀의 모든 멤버 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "멤버 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
        }
    )
    @GetMapping("/{teamId}/members")
    BaseResponse<List<TeamUserResponse>> getTeamMembers(
        @Parameter(description = "팀 ID", example = "1")
        @PathVariable Long teamId
    );

    @Operation(
        summary = "내가 가입한 팀 목록 조회",
        description = "사용자가 가입한 모든 팀 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "팀 목록 조회 성공")
        }
    )
    @GetMapping("/my-teams")
    BaseResponse<List<TeamResponse>> getMyTeams(
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );

    @Operation(
        summary = "멤버 역할 변경",
        description = "팀장만 다른 멤버의 역할을 변경할 수 있습니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "역할 변경 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "팀 또는 사용자를 찾을 수 없음")
        }
    )
    @PatchMapping("/{teamId}/members/{targetUserId}/role")
    BaseResponse<TeamUserResponse> updateMemberRole(
        @Parameter(description = "팀 ID", example = "1")
        @PathVariable Long teamId,
        @Parameter(description = "대상 사용자 ID", example = "2")
        @PathVariable Long targetUserId,
        @Parameter(description = "변경할 역할", example = "MANAGER")
        @RequestParam TeamRole role,
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );
}