package com.matchday.modules.match.application;

import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.MatchApplication;
import com.matchday.modules.match.domain.enums.MatchApplicationStatus;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.api.dto.request.MatchApplicationRequest;
import com.matchday.modules.match.api.dto.response.MatchApplicationResponse;
import com.matchday.modules.match.exception.MatchControllerAdvice;
import com.matchday.modules.match.infrastructure.MatchApplicationRepository;
import com.matchday.modules.match.infrastructure.MatchRepository;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.exception.TeamControllerAdvice;
import com.matchday.modules.team.infrastructure.TeamRepository;
import com.matchday.modules.team.application.TeamUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchApplicationService {
    
    private final MatchApplicationRepository matchApplicationRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final TeamUserService teamUserService;

    // 매치 신청
    @Transactional
    public Long applyToMatch(Long userId, Long matchId, MatchApplicationRequest request) {
        Long teamId = request.getTeamId();
        Team applicantTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));
        teamUserService.validatePermission(teamId, userId);

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode.MATCH_NOT_FOUND));

        // 매치 신청 가능 여부 확인
        validateMatchApplication(match, applicantTeam);

        // 매치 신청 생성
        MatchApplication application = MatchApplication.createApplication(
                match, applicantTeam, request.getMessage());
        
        MatchApplication savedApplication = matchApplicationRepository.save(application);
        
        log.info("매치 신청이 생성되었습니다. 신청 ID: {}, 매치 ID: {}, 신청팀: {}",
                savedApplication.getId(), matchId, applicantTeam.getName());
        
        return savedApplication.getId();
    }

    // 정상적인 신청인지 확인
    private void validateMatchApplication(Match match, Team applicantTeam) {
        // 자신의 매치에는 신청할 수 없음
        if (match.getHomeTeam().equals(applicantTeam)) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_SAME_TEAM);
        }

        // 대기 중인 매치에만 신청 가능
        if (!match.getStatus().equals(MatchStatus.PENDING)) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_ALREADY_ASSIGNED);
        }

        // 매치 신청 가능한 시간, 완료된 매치인지 확인
        if (!match.isAvailableForApplication()) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_TIME_OUT);
        }

        // 중복 신청 확인
        if (matchApplicationRepository.existsByMatchAndApplicantTeam(match, applicantTeam)) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_ALREADY_FOUND);
        }
    }

    // 해당 매치의 신청 목록 조회 TODO: projection
    public List<MatchApplicationResponse> getMatchApplications(Long userId, Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode.MATCH_NOT_FOUND));

        teamUserService.validatePermission(match.getHomeTeam().getId(), userId);

        List<MatchApplication> applications = matchApplicationRepository.findByMatchOrderByCreatedAtDesc(match);
        
        return applications.stream()
                .map(MatchApplicationResponse::of)
                .collect(Collectors.toList());
    }

    // 팀이 신청한 목록 조회 TODO: pagination, projection
    public List<MatchApplicationResponse> getTeamApplications(Long userId, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));
        
        teamUserService.validatePermission(teamId, userId);

        List<MatchApplication> applications = matchApplicationRepository.findByApplicantTeamOrderByCreatedAtDesc(team);
        
        return applications.stream()
                .map(MatchApplicationResponse::of)
                .collect(Collectors.toList());
    }

    // 팀이 받은 전체 신청 목록 조회 TODO: pagination, projection
    public List<MatchApplicationResponse> getReceivedApplications(Long userId, Long teamId) {
        // 팀 조회 및 권한 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));
        
        teamUserService.validatePermission(teamId, userId);

        List<MatchApplication> applications = matchApplicationRepository.findReceivedApplications(team);
        
        return applications.stream()
                .map(MatchApplicationResponse::of)
                .collect(Collectors.toList());
    }

    // 수락 (받은 쪽)
    @Transactional
    public void acceptApplication(Long userId, Long applicationId) {
        MatchApplication application = matchApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode.MATCH_APPLICATION_NOT_FOUND));
        Match match = application.getMatch();

        teamUserService.validatePermission(match.getHomeTeam().getId(), userId);

        // 대기 상태에서만 수락 가능
        if (!match.getStatus().equals(MatchStatus.PENDING)) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_ALREADY_ASSIGNED);
        }

        // 신청한 쪽과 등록한 쪽 모두 상태 변경
        application.accept();
        match.acceptMatch(application.getApplicantTeam());

        // 다른 모든 신청들 거절
        rejectOtherApplications(match, application);

        log.info("매치 신청이 수락되었습니다. 신청 ID: {}, 매치 ID: {}", applicationId, match.getId());
    }

    // 거절 (받은 쪽)
    @Transactional
    public void rejectApplication(Long userId, Long applicationId) {
        MatchApplication application = matchApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode.MATCH_APPLICATION_NOT_FOUND));
        Match match = application.getMatch();
        
        teamUserService.validatePermission(match.getHomeTeam().getId(), userId);

        application.reject();
        
        log.info("매치 신청이 거절되었습니다. 신청 ID: {}", applicationId);
    }

    // 신청 취소 (신청한 쪽)
    @Transactional
    public void cancelApplication(Long userId, Long applicationId) {
        MatchApplication application = matchApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode.MATCH_APPLICATION_NOT_FOUND));
        Team applicantTeam = application.getApplicantTeam();

        // 권한 확인 (신청한 팀의 팀장/매니저만 취소 가능)
        teamUserService.validatePermission(applicantTeam.getId(), userId);

        application.cancel();
        
        log.info("매치 신청이 취소되었습니다. 신청 ID: {}", applicationId);
    }

    // 선택된 신청을 제외한 다른 모든 신청들을 거절 처리 TODO: 쿼리 줄일 수 있을 듯 (벌크 업뎃)
    private void rejectOtherApplications(Match match, MatchApplication acceptedApplication) {
        List<MatchApplication> otherApplications = matchApplicationRepository
                .findByMatchAndStatus(match, MatchApplicationStatus.APPLIED);
        
        otherApplications.stream()
                .filter(application -> !application.getId().equals(acceptedApplication.getId()))
                .forEach(MatchApplication::reject);
        
        log.info("매치 ID: {}의 다른 신청 {}건이 자동으로 거절되었습니다.", 
                match.getId(), otherApplications.size() - 1);
    }
}