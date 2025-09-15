package com.matchday.modules.team.application;

import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.domain.TeamUser;
import com.matchday.modules.team.domain.enums.TeamRole;
import com.matchday.modules.team.api.dto.dto.request.TeamJoinRequest;
import com.matchday.modules.team.api.dto.dto.response.TeamResponse;
import com.matchday.modules.team.api.dto.dto.response.TeamUserResponse;
import com.matchday.modules.team.exception.TeamControllerAdvice;
import com.matchday.modules.team.infrastructure.TeamRepository;
import com.matchday.modules.team.infrastructure.TeamUserRepository;
import com.matchday.modules.user.domain.User;
import com.matchday.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamUserService {
    
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserRepository userRepository;
    
    public Integer getMemberCount(Long teamId) {
        return teamUserRepository.countByTeamId(teamId);
    }
    
    /**
     * 초대코드로 팀 가입
     */
    @Transactional
    public TeamUserResponse joinTeam(Long userId, TeamJoinRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.USER_NOT_FOUND));
//        Integer backNumber = user.getBackNumber();

        // 초대코드로 팀 조회
        Team team = teamRepository.findByInviteCode(request.inviteCode())
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));
        
        // 이미 해당 팀에 가입되어 있는지 확인
        if (teamUserRepository.existsByTeamAndUser(team, user)) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_USER_ALREADY_JOINED);
        }
        
        // 멤버 수 제한 확인
        long currentMemberCount = teamUserRepository.countByTeam(team);
        if (!team.canAddMember((int) currentMemberCount)) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_LIMIT);
        }
        
        // 등번호 중복 확인 -> 이거 필요한건가?
//        if (teamUserRepository.existsByTeamAndBackNumber(team, backNumber)) {
//            throw new TeamControllerAdvice(ResponseCode.BACK_NUMBER_ALREADY_EXIST);
//        }
        
        // 팀 가입
        TeamUser teamUser = TeamUser.joinTeam(team, user, TeamRole.MEMBER);
        TeamUser savedTeamUser = teamUserRepository.save(teamUser);
        
        log.info("팀 가입 완료 - 사용자: {}, 팀: {}",
                userId, team.getId());
        
        return TeamUserResponse.from(savedTeamUser);
    }
    
    /**
     * 팀 탈퇴
     */
    @Transactional
    public void leaveTeam(Long userId, Long teamId) {
        TeamUser teamUser = teamUserRepository.findByTeamIdAndUserId(teamId, userId)
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_USER_NOT_FOUND));
        
        // 팀장은 탈퇴 불가 (팀을 삭제하거나 팀장을 위임해야 함)
        if (teamUser.getRole() == TeamRole.LEADER) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_LEADER_CANNOT_LEAVE);
        }
        
        teamUserRepository.delete(teamUser);
        
        log.info("팀 탈퇴 완료 - 사용자: {}, 팀: {}", userId, teamId);
    }
    
    /**
     * 팀 멤버 목록 조회
     */
    public List<TeamUserResponse> getTeamMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));
        
        List<TeamUser> teamUsers = teamUserRepository.findByTeam(team);
        return teamUsers.stream()
            .map(TeamUserResponse::from)
            .toList();
    }
    
    /**
     * 사용자가 가입한 팀 목록 조회
     */
    public List<TeamResponse> getUserTeams(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.USER_NOT_FOUND));
        
        List<TeamUser> teamUsers = teamUserRepository.findByUser(user);
        return teamUsers.stream()
            .map(teamUser -> {
                Team team = teamUser.getTeam();
                Integer memberCount = getMemberCount(team.getId());
                return TeamResponse.from(team, memberCount);
            })
            .toList();
    }
    
    /**
     * 팀장으로 멤버 추가 (처음 팀 만들 때 사용)
     */
    @Transactional
    public void addTeamLeader(Team team, User user) {
        TeamUser teamLeader = TeamUser.createLeader(team, user);
        teamUserRepository.save(teamLeader);
        
        log.info("팀 생성 및 팀장 설정 완료 - 사용자: {}, 팀: {}", user.getId(), team.getId());
    }
    
    /**
     * 멤버 역할 변경(팀장만 가능)
     * 대상자를 팀장으로 변경하는 경우 본인은 일반 멤버로 강등됨
     */
    @Transactional
    public TeamUserResponse updateMemberRole(Long teamId, Long targetUserId, TeamRole newRole, Long requestUserId) {
        // 요청자가 팀장인지 확인
        TeamUser requestTeamUser = teamUserRepository.findByTeamIdAndUserId(teamId, requestUserId)
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_USER_NOT_FOUND));
        if (!requestTeamUser.isLeader()) {
            throw new TeamControllerAdvice(ResponseCode._UNAUTHORIZED);
        }

        // 대상 조회
        TeamUser targetTeamUser = teamUserRepository.findByTeamIdAndUserId(teamId, targetUserId)
            .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_USER_NOT_FOUND));
        
        // 팀장 권한을 위임하는 경우
        if (newRole == TeamRole.LEADER) {
            // 기존 팀장을 일반 멤버로 변경
            requestTeamUser.changeRole(TeamRole.MEMBER);
            teamUserRepository.save(requestTeamUser);
            
            log.info("팀장 권한 위임으로 인한 강등 - 팀: {}, 기존 팀장: {} -> MEMBER", 
                    teamId, requestUserId);
        }
        
        // 역할 변경
        targetTeamUser.changeRole(newRole);
        TeamUser updatedTeamUser = teamUserRepository.save(targetTeamUser);
        
        log.info("멤버 역할 변경 완료 - 팀: {}, 대상자: {}, 새 역할: {}", 
                teamId, targetUserId, newRole);
        
        return TeamUserResponse.from(updatedTeamUser);
    }

    public void kickMember(Long requestUserId, Long targetUserId, Long teamId) {
        validatePermission(teamId, requestUserId);

        TeamUser teamUser = teamUserRepository.findByTeamIdAndUserId(teamId, targetUserId)
                .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_USER_NOT_FOUND));

        teamUserRepository.delete(teamUser);
    }
    
    /**
     * 운영진 권한 확인(운영진 or 팀장) TODO: 시큐리티 컨텍스트로 권한 확인할 수 있는 로직 생각해보기
     */
    public void validatePermission(Long teamId, Long userId) {
        TeamUser teamUser = teamUserRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_USER_NOT_FOUND));
        
        // 권한 확인
        if (!teamUser.hasManagementAuthority()) {
            throw new TeamControllerAdvice(ResponseCode._UNAUTHORIZED);
        }
    }
}