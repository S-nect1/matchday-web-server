package com.matchday.modules.team.controller;

import com.matchday.common.dto.response.PagedResponse;
import com.matchday.common.entity.BaseResponse;
import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamType;
import com.matchday.modules.team.dto.request.TeamCreateRequest;
import com.matchday.modules.team.dto.request.TeamSearchRequest;
import com.matchday.modules.team.dto.request.TeamUpdateRequest;
import com.matchday.modules.team.dto.response.TeamListResponse;
import com.matchday.modules.team.dto.response.TeamResponse;
import com.matchday.modules.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController implements TeamControllerDocs {
    
    private final TeamService teamService;

    @PostMapping
    public BaseResponse<Long> createTeam(@Valid @RequestBody TeamCreateRequest request, @RequestHeader("User-Id") Long userId) {
        return BaseResponse.onSuccess(teamService.createTeam(userId, request), ResponseCode.OK);
    }

    @GetMapping("/{teamId}")
    public BaseResponse<TeamResponse> getTeamDetails(@PathVariable Long teamId) {
        TeamResponse response = teamService.getTeamDetails(teamId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    @PatchMapping("/{teamId}")
    public BaseResponse<TeamResponse> updateTeam(
            @PathVariable Long teamId,
            @RequestHeader("User-Id") Long userId,
            @Valid @RequestBody TeamUpdateRequest request) {
        
        TeamResponse response = teamService.updateTeam(teamId, request, userId);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }

    @DeleteMapping("/{teamId}")
    public BaseResponse<String> deleteTeam(
            @PathVariable Long teamId,
            @RequestHeader("User-Id") Long userId) {
        
        teamService.deleteTeam(teamId, userId);
        return BaseResponse.onSuccess("팀이 삭제되었습니다.", ResponseCode.OK);
    }

    @GetMapping
    public BaseResponse<PagedResponse<TeamListResponse>> getTeamList(
            @RequestParam(required = false) City city,
            @RequestParam(required = false) District district,
            @RequestParam(required = false) TeamType type,
            @RequestParam(required = false) GroupGender gender,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer ageGroup,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        TeamSearchRequest searchRequest = new TeamSearchRequest();

        searchRequest.setCity(city);
        searchRequest.setDistrict(district);
        searchRequest.setType(type);
        searchRequest.setGender(gender);
        searchRequest.setKeyword(StringUtils.hasText(keyword) ? keyword.trim() : null);
        searchRequest.setAgeGroup(ageGroup);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSort(sort);
        searchRequest.setDirection(direction);
        
        PagedResponse<TeamListResponse> response = teamService.getTeamList(searchRequest);
        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }
}