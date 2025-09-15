package com.matchday.modules.team.api.spec;

import com.matchday.common.entity.BaseResponse;
import com.matchday.modules.match.api.dto.dto.response.MatchApplicationResponse;
import com.matchday.modules.match.api.dto.dto.response.MatchListResponse;
import com.matchday.security.filter.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "TeamMatch", description = "팀 매치 관리 API")
public interface TeamMatchControllerDocs {

    @Operation(
        summary = "팀이 등록한 매치 목록 조회",
        description = "특정 팀이 등록한 매치 목록을 조회합니다. 해당 팀의 멤버만 조회할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "팀 매치 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MatchListResponse.class))
        ),
        @ApiResponse(responseCode = "403", description = "권한 없음 - 해당 팀의 멤버가 아님"),
        @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    BaseResponse<List<MatchListResponse>> getTeamMatches(
        @Parameter(description = "팀 ID", required = true)
        @PathVariable Long teamId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );

    @Operation(
        summary = "팀이 신청한 매치 목록 조회",
        description = "특정 팀이 다른 팀에게 신청한 매치 목록을 조회합니다. 해당 팀의 관리자만 조회할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "팀 신청 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MatchApplicationResponse.class))
        ),
        @ApiResponse(responseCode = "403", description = "권한 없음 - 해당 팀의 관리자가 아님"),
        @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    BaseResponse<List<MatchApplicationResponse>> getTeamApplications(
        @Parameter(description = "팀 ID", required = true)
        @PathVariable Long teamId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );

    @Operation(
        summary = "팀이 받은 신청 목록 조회",
        description = "특정 팀이 등록한 매치에 대해 받은 신청 목록을 조회합니다. 해당 팀의 관리자만 조회할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "받은 신청 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MatchApplicationResponse.class))
        ),
        @ApiResponse(responseCode = "403", description = "권한 없음 - 해당 팀의 관리자가 아님"),
        @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    BaseResponse<List<MatchApplicationResponse>> getReceivedApplications(
        @Parameter(description = "팀 ID", required = true)
        @PathVariable Long teamId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );
}