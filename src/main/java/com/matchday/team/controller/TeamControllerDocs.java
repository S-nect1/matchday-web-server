package com.matchday.team.controller;

import com.matchday.global.dto.response.PagedResponse;
import com.matchday.global.entity.BaseResponse;
import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.enums.GroupGender;
import com.matchday.team.domain.enums.TeamType;
import com.matchday.team.dto.request.TeamCreateRequest;
import com.matchday.team.dto.request.TeamUpdateRequest;
import com.matchday.team.dto.response.TeamListResponse;
import com.matchday.team.dto.response.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "팀 관리", description = "팀 생성, 조회, 수정, 삭제 API")
public interface TeamControllerDocs {

    @Operation(
        summary = "팀 생성",
        description = "새로운 팀을 생성합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "팀 생성 성공")
        }
    )
    BaseResponse<Long> createTeam(
        @Valid @RequestBody TeamCreateRequest request, 
        @RequestHeader("User-Id") Long userId
    );

    @Operation(
        summary = "팀 상세 조회",
        description = "팀 ID로 특정 팀의 상세 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "팀 조회 성공"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
        }
    )

    BaseResponse<TeamResponse> getTeamDetails(
        @Parameter(description = "팀 ID", example = "1")
        @PathVariable Long teamId
    );

    @Operation(
        summary = "팀 정보 수정",
        description = "팀장만 팀 정보를 수정할 수 있습니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "팀 수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
        }
    )
    BaseResponse<TeamResponse> updateTeam(
        @Parameter(description = "팀 ID", example = "1")
        @PathVariable Long teamId,
        @RequestHeader("User-Id") Long userId,
        @Valid @RequestBody TeamUpdateRequest request
    );

    @Operation(
        summary = "팀 삭제",
        description = "팀장만 팀을 삭제할 수 있습니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "팀 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
        }
    )
    BaseResponse<String> deleteTeam(
        @Parameter(description = "팀 ID", example = "1")
        @PathVariable Long teamId,
        @RequestHeader("User-Id") Long userId
    );

    @Operation(
        summary = "팀 목록 조회",
        description = "다양한 조건으로 팀 목록을 조회합니다. 모든 파라미터는 선택사항입니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "팀 목록 조회 성공")
        }
    )
    BaseResponse<PagedResponse<TeamListResponse>> getTeamList(
        @Parameter(description = "시/도 필터", example = "SEOUL") 
        @RequestParam(required = false) City city,
        
        @Parameter(description = "구/군 필터", example = "SEOUL_GANGNAM")
        @RequestParam(required = false) District district,
        
        @Parameter(description = "팀 유형 필터", example = "CLUB") 
        @RequestParam(required = false) TeamType type,
        
        @Parameter(description = "성별 필터", example = "MIXED") 
        @RequestParam(required = false) GroupGender gender,
        
        @Parameter(description = "팀명 검색 키워드", example = "FC") 
        @RequestParam(required = false) String keyword,
        
        @Parameter(description = "페이지 번호", example = "0") 
        @RequestParam(defaultValue = "0") int page,
        
        @Parameter(description = "페이지 크기", example = "20") 
        @RequestParam(defaultValue = "20") int size,
        
        @Parameter(description = "정렬 기준(생성일/이름)", example = "createdAt")
        @RequestParam(defaultValue = "createdAt") String sort,
        
        @Parameter(description = "정렬 방향(오름차순/내림차순)", example = "desc")
        @RequestParam(defaultValue = "desc") String direction
    );
}