package com.matchday.modules.match.api.spec;

import com.matchday.common.entity.BaseResponse;
import com.matchday.modules.match.api.dto.request.MatchApplicationRequest;
import com.matchday.modules.match.api.dto.response.MatchApplicationResponse;
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

@Tag(name = "MatchApplication", description = "매치 신청 관리 API")
public interface MatchApplicationControllerDocs {

    @Operation(
        summary = "매치 신청", 
        description = "다른 팀이 등록한 매치에 신청합니다. 팀장 또는 부팀장만 신청할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "매치 신청 성공",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "이미 신청한 매치이거나 신청할 수 없는 매치"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "매치 또는 팀을 찾을 수 없음")
    })
    BaseResponse<Long> applyToMatch(
        @Parameter(description = "매치 ID", required = true)
        @PathVariable Long matchId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
        @Parameter(description = "매치 신청 요청", required = true)
        @Valid @RequestBody MatchApplicationRequest request
    );

    @Operation(
        summary = "매치 신청 목록 조회", 
        description = "등록한 매치에 대한 신청 목록을 조회합니다. 홈팀의 팀장 또는 부팀장만 조회할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "매치 신청 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MatchApplicationResponse.class))
        ),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음")
    })
    BaseResponse<List<MatchApplicationResponse>> getMatchApplications(
        @Parameter(description = "매치 ID", required = true)
        @PathVariable Long matchId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );


    @Operation(
        summary = "매치 신청 수락", 
        description = "매치 신청을 수락합니다. 홈팀의 팀장 또는 부팀장만 수락할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매치 신청 수락 성공"),
        @ApiResponse(responseCode = "400", description = "이미 처리된 신청이거나 수락할 수 없는 상태"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "신청을 찾을 수 없음")
    })
    BaseResponse<String> acceptApplication(
        @Parameter(description = "신청 ID", required = true)
        @PathVariable Long applicationId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );

    @Operation(
        summary = "매치 신청 거절", 
        description = "매치 신청을 거절합니다. 홈팀의 팀장 또는 부팀장만 거절할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매치 신청 거절 성공"),
        @ApiResponse(responseCode = "400", description = "이미 처리된 신청이거나 거절할 수 없는 상태"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "신청을 찾을 수 없음")
    })
    BaseResponse<String> rejectApplication(
        @Parameter(description = "신청 ID", required = true)
        @PathVariable Long applicationId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );

    @Operation(
        summary = "매치 신청 취소", 
        description = "매치 신청을 취소합니다. 신청한 팀의 팀장 또는 부팀장만 취소할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매치 신청 취소 성공"),
        @ApiResponse(responseCode = "400", description = "이미 처리된 신청이거나 취소할 수 없는 상태"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "신청을 찾을 수 없음")
    })
    BaseResponse<String> cancelApplication(
        @Parameter(description = "신청 ID", required = true)
        @PathVariable Long applicationId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    );
}