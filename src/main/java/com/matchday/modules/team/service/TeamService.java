package com.matchday.modules.team.service;

import com.matchday.common.dto.response.PagedResponse;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.dto.request.TeamCreateRequest;
import com.matchday.modules.team.dto.request.TeamSearchRequest;
import com.matchday.modules.team.dto.request.TeamUpdateRequest;
import com.matchday.modules.team.dto.response.TeamListResponse;
import com.matchday.modules.team.dto.response.TeamResponse;
import com.matchday.modules.team.exception.TeamControllerAdvice;
import com.matchday.modules.team.repository.TeamRepository;
import com.matchday.modules.user.domain.User;
import com.matchday.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
    
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamUserService teamUserService;

    /**
     * 팀 생성
     */
    @Transactional
    public Long createTeam(Long userId, TeamCreateRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.USER_NOT_FOUND));

        // 팀 생성
        Team team = Team.createTeam(
            request.name(),
            request.description(),
            request.type(),
            request.activityArea() != null ? request.activityArea().city() : null,
            request.activityArea() != null ? request.activityArea().district() : null,
            request.uniformColorHex(),
            request.hasBall(),
            request.gender(),
            request.memberLimit(),
            request.bankName(),
            request.bankAccount(),
            request.profileImageUrl()
        );
        
        // FIXME: 초대코드가 중복될 경우 재생성 (다른 로직 있나 고민 좀 해보기)
        while (teamRepository.existsByInviteCode(team.getInviteCode())) {
            team.regenerateInviteCode();
        }
        Team savedTeam = teamRepository.save(team);
        
        // 팀장으로 팀에 가입 (TeamUserService 위임)
        teamUserService.addTeamLeader(savedTeam, user);
        
        log.info("팀 생성 완료 - 사용자: {}, 팀: {}", userId, savedTeam.getId());
        
        return savedTeam.getId();
    }

    /**
     * 팀 상세 조회
     */
    public Team getTeam(Long teamId) {

        return teamRepository.findById(teamId)
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));
    }

    /**
     * 팀 상세 조회 (memberCount 포함)
     */
    public TeamResponse getTeamDetails(Long teamId) {
        Team team = getTeam(teamId);
        Integer memberCount = teamUserService.getMemberCount(teamId);
        
        return TeamResponse.from(team, memberCount);
    }
    
    /**
     * 팀 목록 조회 (동적 검색 조건 적용)
     */
    public PagedResponse<TeamListResponse> getTeamList(TeamSearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(
            searchRequest.getPage(), 
            searchRequest.getSize()
        );
        
        Page<TeamListResponse> teamsPage = teamRepository.findTeamsByConditions(searchRequest, pageable);
        
        log.info("팀 목록 조회 완료 - 조건: {}, 결과: {}개", searchRequest, teamsPage.getTotalElements());
        
        return PagedResponse.of(teamsPage);
    }
    
    /**
     * 팀 정보 수정
     */
    @Transactional
    public TeamResponse updateTeam(Long teamId, TeamUpdateRequest request, Long requestUserId) {
        Team team = getTeam(teamId);
        
        // 수정 권한 확인 (팀장만 가능)
        teamUserService.validatePermission(teamId, requestUserId);
        
        // 팀 정보 수정 (null이 아닌 필드만 업데이트)
        if (request.name() != null) {
            team.updateName(request.name());
        }
        if (request.description() != null) {
            team.updateDescription(request.description());
        }
        if (request.activityArea() != null) {
            team.updateActivityArea(
                request.activityArea().city(),
                request.activityArea().district()
            );
        }
        if (request.uniformColorHex() != null) {
            team.updateUniformColor(request.uniformColorHex());
        }
        if (request.hasBall() != null) {
            team.updateHasBall(request.hasBall());
        }
        if (request.memberLimit() != null) {
            team.updateMemberLimit(request.memberLimit());
        }
        if (request.profileImageUrl() != null) {
            team.updateProfileImage(request.profileImageUrl());
        }
        
        Team updatedTeam = teamRepository.save(team);
        
        log.info("팀 정보 수정 완료 - 팀: {}, 수정자: {}", teamId, requestUserId);
        
        Integer memberCount = teamUserService.getMemberCount(teamId);
        return TeamResponse.from(updatedTeam, memberCount);
    }
    
    /**
     * 팀 삭제
     */
    @Transactional
    public void deleteTeam(Long teamId, Long requestUserId) {
        // 팀 조회
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));
        
        // 삭제 권한 확인 (팀장만 가능)
        teamUserService.validatePermission(teamId, requestUserId);
        
        // 팀 관련 데이터 삭제는 cascade 설정에 따라 자동 처리
        teamRepository.delete(team);
        
        log.info("팀 삭제 완료 - 팀: {}, 삭제자: {}", teamId, requestUserId);
    }
}