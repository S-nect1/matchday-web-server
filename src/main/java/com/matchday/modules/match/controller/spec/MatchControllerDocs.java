package com.matchday.modules.match.controller.spec;

import com.matchday.common.entity.BaseResponse;
import com.matchday.modules.match.dto.request.MatchCreateRequest;
import com.matchday.modules.match.dto.response.MatchListResponse;
import com.matchday.modules.match.dto.response.MatchResponse;
import com.matchday.security.filter.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Match", description = "매치 관리 API")
public interface MatchControllerDocs {

    @Operation(
        summary = "매치 등록", 
        description = "새로운 매치를 등록합니다. 팀장 또는 운영진만 등록할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "매치 등록 성공",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    BaseResponse<Long> createMatch(
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
        @Parameter(description = "매치 생성 요청", required = true)
        @Valid @RequestBody MatchCreateRequest request
    );

    @Operation(
        summary = "매치 상세 조회", 
        description = "매치 ID로 매치의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "매치 조회 성공",
            content = @Content(schema = @Schema(implementation = MatchResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음")
    })
    BaseResponse<MatchResponse> getMatchDetails(
        @Parameter(description = "매치 ID", required = true)
        @PathVariable Long matchId
    );

    @Operation(
        summary = "매칭 가능한 매치 목록 조회", 
        description = "현재 신청 가능한 매치 목록을 조회합니다. (상대팀이 없고, 미래 일정인 매치)"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "매치 목록 조회 성공",
        content = @Content(schema = @Schema(implementation = MatchListResponse.class))
    )
    BaseResponse<List<MatchListResponse>> getAvailableMatches();


    @Operation(
        summary = "매치 삭제", 
        description = "매치를 삭제합니다. 홈팀의 팀장 또는 부팀장만 삭제할 수 있으며, 이미 매칭된 매치는 삭제할 수 없습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매치 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "이미 매칭된 매치는 삭제할 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음")
    })
    BaseResponse<String> cancelMatch(
        @Parameter(description = "매치 ID", required = true)
        @PathVariable Long matchId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );
}