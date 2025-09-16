package com.matchday.modules.match.api.spec;

import com.matchday.common.dto.response.PagedResponse;
import com.matchday.common.entity.BaseResponse;
import com.matchday.modules.match.api.dto.request.MatchCreateRequest;
import com.matchday.modules.match.api.dto.request.MatchUpdateRequest;
import com.matchday.modules.match.api.dto.request.MatchResultRequest;
import com.matchday.modules.match.api.dto.response.MatchListResponse;
import com.matchday.modules.match.api.dto.response.MatchResponse;
import com.matchday.modules.match.api.dto.response.MonthlyMatchResponse;
import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.SportsType;
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

import java.time.LocalDate;

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
        description = "현재 신청 가능한 매치 목록을 조회합니다. 필터링 및 페이지네이션을 지원합니다."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "매치 목록 조회 성공",
        content = @Content(schema = @Schema(implementation = PagedResponse.class))
    )
    BaseResponse<PagedResponse<MatchListResponse>> getAvailableMatches(
        @Parameter(description = "스포츠 종목")
        @RequestParam(required = false) SportsType sportsType,
        @Parameter(description = "매치 규모")
        @RequestParam(required = false) MatchSize matchSize,
        @Parameter(description = "검색 시작 날짜")
        @RequestParam(required = false) LocalDate startDate,
        @Parameter(description = "검색 종료 날짜")
        @RequestParam(required = false) LocalDate endDate,
        @Parameter(description = "검색 키워드 (팀명, 장소명)")
        @RequestParam(required = false) String keyword,
        @Parameter(description = "페이지 번호 (0부터 시작)")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기")
        @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "정렬 기준")
        @RequestParam(defaultValue = "createdAt") String sort,
        @Parameter(description = "정렬 방향")
        @RequestParam(defaultValue = "desc") String direction
    );

    @Operation(
        summary = "내 매치 목록 조회",
        description = "사용자가 가입한 모든 팀의 확정된 매치/신청 목록을 월별로 조회합니다. 일자별로 매치 ID 목록을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "내 매치 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MonthlyMatchResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    BaseResponse<MonthlyMatchResponse> getAllMyMatches(
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
        @Parameter(description = "조회할 연도 (기본값: 현재 연도)")
        @RequestParam(required = false) Integer year,
        @Parameter(description = "조회할 월 (기본값: 현재 월)")
        @RequestParam(required = false) Integer month
    );

    @Operation(
        summary = "매치 수정", 
        description = "매치 정보를 수정합니다. 홈팀의 팀장 또는 부팀장만 수정할 수 있으며, PENDING 상태의 매치만 수정 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매치 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 이미 확정된 매치"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음")
    })
    BaseResponse<String> updateMatch(
        @Parameter(description = "매치 ID", required = true)
        @PathVariable Long matchId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
        @Parameter(description = "매치 수정 요청", required = true)
        @Valid @RequestBody MatchUpdateRequest request
    );

    @Operation(
        summary = "매치 결과 기록",
        description = "확정된 매치의 점수를 기록합니다. 매치 종료 후 48시간 이내에만 기록 가능하며, 매치에 참여한 팀의 멤버만 기록할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매치 결과 기록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 기록 기한 초과"),
        @ApiResponse(responseCode = "403", description = "권한 없음 (매치 참여자가 아님)"),
        @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음")
    })
    BaseResponse<String> recordMatchResult(
        @Parameter(description = "매치 ID", required = true)
        @PathVariable Long matchId,
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserPrincipal userPrincipal,
        @Parameter(description = "매치 결과 요청", required = true)
        @Valid @RequestBody MatchResultRequest request
    );

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