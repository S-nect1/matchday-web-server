package com.matchday.match.domain;

import com.matchday.global.entity.BaseEntity;
import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.match.domain.enums.MatchApplicationStatus;
import com.matchday.match.exception.advice.MatchControllerAdvice;
import com.matchday.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "match_application")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchApplication extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Team applicantTeam;

    @Column(length = 200)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchApplicationStatus status;

    private LocalDateTime processedAt;

    // 매치 신청 생성 팩토리 메서드
    public static MatchApplication createApplication(Match match, Team applicantTeam, String message) {
        MatchApplication application = new MatchApplication();

        application.match = match;
        application.applicantTeam = applicantTeam;
        application.message = message;
        application.status = MatchApplicationStatus.APPLIED;

        return application;
    }

    // 처리 가능한 상태인지 확인(신청 상태에서만 처리 가능)
    private void validateCanProcess() {
        if (this.status != MatchApplicationStatus.APPLIED) {
            throw new MatchControllerAdvice(ResponseCode.MATCH_APPLICATION_INVALID_STATUS);
        }
    }

    // 매치 수락
    public void accept() {
        validateCanProcess();
        this.status = MatchApplicationStatus.ACCEPTED;
        this.processedAt = LocalDateTime.now();
    }

    // 매치 거절
    public void reject() {
        validateCanProcess();
        this.status = MatchApplicationStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }

    // 매치 취소
    public void cancel() {
        validateCanProcess();
        this.status = MatchApplicationStatus.CANCELED;
        this.processedAt = LocalDateTime.now();
    }

    // 매치 신청 상태 확인
    public boolean isApplied() {
        return this.status == MatchApplicationStatus.APPLIED;
    }
    public boolean isAccepted() {
        return this.status == MatchApplicationStatus.ACCEPTED;
    }
    public boolean isRejected() {
        return this.status == MatchApplicationStatus.REJECTED;
    }
    public boolean isCanceled() {
        return this.status == MatchApplicationStatus.CANCELED;
    }

}