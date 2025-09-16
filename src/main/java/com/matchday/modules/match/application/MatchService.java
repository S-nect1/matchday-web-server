 package com.matchday.modules.match.application;

import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.match.domain.Match;
import com.matchday.modules.match.domain.enums.MatchStatus;
import com.matchday.common.dto.response.PagedResponse;
import com.matchday.modules.match.api.dto.projection.MatchListProjection;
import com.matchday.modules.match.api.dto.request.MatchCreateRequest;
import com.matchday.modules.match.api.dto.request.MatchSearchRequest;
import com.matchday.modules.match.api.dto.request.MatchUpdateRequest;
import com.matchday.modules.match.api.dto.request.MatchResultRequest;
import com.matchday.modules.match.api.dto.response.MatchListResponse;
import com.matchday.modules.match.api.dto.response.MatchResponse;
import com.matchday.modules.match.api.dto.response.MonthlyMatchResponse;
import com.matchday.modules.match.api.dto.response.TeamConfirmedMatchResponse;
import com.matchday.modules.match.api.dto.projection.TeamConfirmedMatchProjection;
import com.matchday.modules.match.exception.MatchControllerAdvice;
import com.matchday.modules.match.infrastructure.MatchRepository;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.exception.TeamControllerAdvice;
import com.matchday.modules.team.infrastructure.TeamRepository;
import com.matchday.modules.team.infrastructure.TeamUserRepository;
import com.matchday.modules.team.application.TeamUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // PENDING 매치 조회(필터 조건 추가)
    public PagedResponse<MatchListResponse> getAvailableMatches(MatchSearchRequest searchRequest) {
        Sort sort = Sort.by(Sort.Direction.fromString(searchRequest.getDirection()), searchRequest.getSort());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        Page<MatchListProjection> availableMatches = matchRepository.findAvailableMatches(
                MatchStatus.PENDING,
                searchRequest,
                pageable
        );
        
        List<MatchListResponse> content = availableMatches.getContent().stream()
                .map(MatchListResponse::of)
                .collect(Collectors.toList());
        
        return PagedResponse.<MatchListResponse>builder()
                .content(content)
                .page(availableMatches.getNumber())
                .size(availableMatches.getSize())
                .totalElements(availableMatches.getTotalElements())
                .totalPages(availableMatches.getTotalPages())
                .first(availableMatches.isFirst())
                .last(availableMatches.isLast())
                .empty(availableMatches.isEmpty())
                .build();
    }

    // 해당 팀의 모든 등록한 매치 조회 TODO: pagination, projection
    public List<MatchListResponse> getTeamMatches(Long userId, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamControllerAdvice(ResponseCode.TEAM_NOT_FOUND));

        teamUserRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode._FORBIDDEN));

        List<Match> teamMatches = matchRepository.findByHomeTeamOrderByCreatedDateDesc(team);
        
        return teamMatches.stream()
                .map(MatchListResponse::of)
                .collect(Collectors.toList());
    }

    // 월별 확정된 매치 목록 
    public MonthlyMatchResponse getMonthlyMatches(Long userId, LocalDate startDate, LocalDate endDate) {
        return matchRepository.findMonthlyMatches(userId, startDate, endDate);
    }

    // 특정 팀의 확정된 매치 목록 조회
    public PagedResponse<TeamConfirmedMatchResponse> getTeamConfirmedMatches(
            Long userId, Long teamId, int page, int size, String sortBy, String direction) {
        
        teamUserRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode._FORBIDDEN));
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TeamConfirmedMatchProjection> confirmedMatches = 
                matchRepository.findTeamConfirmedMatches(teamId, pageable);
        
        List<TeamConfirmedMatchResponse> content = confirmedMatches.getContent().stream()
                .map(TeamConfirmedMatchResponse::of)
                .collect(Collectors.toList());
        
        return PagedResponse.<TeamConfirmedMatchResponse>builder()
                .content(content)
                .page(confirmedMatches.getNumber())
                .size(confirmedMatches.getSize())
                .totalElements(confirmedMatches.getTotalElements())
                .totalPages(confirmedMatches.getTotalPages())
                .first(confirmedMatches.isFirst())
                .last(confirmedMatches.isLast())
                .empty(confirmedMatches.isEmpty())
                .build();
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


    // 매치 결과 기록 (48시간 이내에만 가능)
    @Transactional
    public void recordMatchResult(Long matchId, Long userId, MatchResultRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode.MATCH_NOT_FOUND));

        if (match.getStatus() != MatchStatus.CONFIRMED) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_NOT_CONFIRMED);
        }

        // 매치에 참여한 팀의 운영진인지 확인
        teamUserService.validateBothPermission(match.getHomeTeam().getId(), match.getAwayTeam().getId(), userId);

        // 매치 종료 후 48시간 이내인지 확인
        validateResultRecordingDeadline(match);

        // 매치 결과 업데이트
        match.updateScore(request.getHomeScore(), request.getAwayScore());
        match.completeMatch();

        log.info("매치 결과가 기록되었습니다. 매치 ID: {}, 점수: {} - {}", 
                matchId, request.getHomeScore(), request.getAwayScore());
    }

    // 매치 결과 기록 기한 검증 (매치 종료 후 48시간 이내)
    private void validateResultRecordingDeadline(Match match) {
        LocalDateTime matchEndTime = LocalDateTime.of(match.getDate(), match.getEndTime());
        LocalDateTime deadline = matchEndTime.plusHours(48);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(deadline)) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_TIME_OUT);
        }
    }

    // 매치 수정
    @Transactional
    public void updateMatch(Long matchId, Long userId, MatchUpdateRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchControllerAdvice(ResponseCode.MATCH_NOT_FOUND));

        teamUserService.validatePermission(match.getHomeTeam().getId(), userId);

        // TODO: 종목과 사이즈 올바른지 검사 추가
        if (request.getSportsType() != null) {
            match.updateSportsType(request.getSportsTypeEnum());
        }
        if (request.getMatchSize() != null) {
            match.updateMatchSize(request.getMatchSize());
        }

        // 시간 업데이트 (시간 충돌 검사 포함)
        if (request.getMatchDateTime() != null && request.getEndDateTime() != null) {
            LocalDate matchDate = request.getMatchDateTime().toLocalDate();
            LocalTime startTime = request.getMatchDateTime().toLocalTime();
            LocalTime endTime = request.getEndDateTime().toLocalTime();

            List<Long> conflictingMatches = matchRepository.findConflictingMatches(
                    match.getHomeTeam(), matchDate, startTime, endTime);

            conflictingMatches = conflictingMatches.stream()
                    .filter(id -> !id.equals(matchId))
                    .toList();

            if (!conflictingMatches.isEmpty()) {
                throw new MatchControllerAdvice(ResponseCode.MATCH_DUPLICATED);
            }

            match.updateMatchTime(matchDate, startTime, endTime);
        }

        if (request.getMatchLocation() != null) {
            updateMatchLocation(match, request.getMatchLocation());
        }
        if (request.getFee() != null) {
            match.updateFee(request.getFee());
        }
        if (request.getUniformColor() != null) {
            match.updateHomeColor(request.getUniformColor());
        }
        if (request.getHasBall() != null) {
            match.updateHasBall(request.getHasBall());
        }

        log.info("매치가 수정되었습니다. 매치 ID: {}, 팀: {}", matchId, match.getHomeTeam().getName());
    }

    // 매치 장소 업데이트 헬퍼 메서드
    private void updateMatchLocation(Match match, MatchUpdateRequest.MatchLocation location) {
        if (location.getAddress() != null && location.getDetailAddress() != null) {
            AddressParsingService.AddressInfo addressInfo = addressParsingService.parseAddress(
                    location.getAddress());
            
            match.updateLocation(
                    addressInfo.getCity(),
                    addressInfo.getDistrict(),
                    location.getDetailAddress(),
                    location.getZipCode()
            );
        }
    }
}