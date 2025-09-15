 package com.matchday.modules.match.service;

import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.modules.match.dto.projection.MatchListProjection;
import com.matchday.modules.match.dto.request.MatchCreateRequest;
import com.matchday.modules.match.dto.response.MatchListResponse;
import com.matchday.modules.match.dto.response.MatchResponse;
import com.matchday.modules.match.exception.MatchControllerAdvice;
import com.matchday.modules.match.repository.MatchRepository;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.exception.TeamControllerAdvice;
import com.matchday.modules.team.repository.TeamRepository;
import com.matchday.modules.team.repository.TeamUserRepository;
import com.matchday.modules.team.service.TeamUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final AddressParsingService addressParsingService;
    private final TeamUserService teamUserService;

    // 매치 생성
    @Transactional
    public Long createMatch(Long userId, MatchCreateRequest request) {
        Long teamId = request.getTeamId();
        Team homeTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));

        teamUserService.validatePermission(teamId, userId);

        // 주소 파싱
        AddressParsingService.AddressInfo addressInfo = addressParsingService.parseAddress(
                request.getMatchLocation().getAddress());
        
        // 시간이 겹치는 매치가 있는지 확인
        LocalDate matchDate = request.getMatchDateTime().toLocalDate();
        LocalTime startTime = request.getMatchDateTime().toLocalTime();
        LocalTime endTime = request.getEndDateTime().toLocalTime();
        
        List<Long> conflictingMatches = matchRepository.findConflictingMatches(
                homeTeam, matchDate, startTime, endTime);
        if (!conflictingMatches.isEmpty()) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_DUPLICATED);
        }

        Match match = Match.createMatch(
                homeTeam,
                addressInfo.getCity(),
                addressInfo.getDistrict(),
                request.getMatchLocation().getDetailAddress(),
                matchDate,
                startTime,
                endTime,
                request.getFee(),
                request.getMatchSize(),
                request.getMatchLocation().getZipCode(),
                request.getUniformColor(),
                request.getHasBall(),
                request.getSportsTypeEnum(),
                homeTeam.getName() + "의 매치입니다."
        );

        Match savedMatch = matchRepository.save(match);
        log.info("매치가 생성되었습니다. 매치 ID: {}, 팀: {}", savedMatch.getId(), homeTeam.getName());
        
        return savedMatch.getId();
    }

    // 매치 상세 페이지 조회
    public MatchResponse getMatchDetails(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode.MATCH_NOT_FOUND));
        
        return MatchResponse.of(match);
    }

    // PENDING 매치 조회 (신청 가능한 매치)
    public List<MatchListResponse> getAvailableMatches() {
        List<MatchListProjection> availableMatches = matchRepository.findAvailableMatches(MatchStatus.PENDING);
        
        return availableMatches.stream()
                .map(MatchListResponse::of)
                .collect(Collectors.toList());
    }

    // 해당 팀의 모든 등록한 매치 조회
    public List<MatchListResponse> getTeamMatches(Long userId, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));

        // 사용자가 해당 팀의 멤버인지 확인
        teamUserRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode._FORBIDDEN));

        List<Match> teamMatches = matchRepository.findByHomeTeamOrderByCreatedDateDesc(team);
        
        return teamMatches.stream()
                .map(MatchListResponse::of)
                .collect(Collectors.toList());
    }

    // 매치 등록 취소
    @Transactional
    public void cancelMatch(Long matchId, Long userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode.MATCH_NOT_FOUND));

        teamUserService.validatePermission(match.getHomeTeam().getId(), userId);

        match.cancelMatch();
        log.info("매치가 취소되었습니다. 매치 ID: {}", matchId);
    }
}