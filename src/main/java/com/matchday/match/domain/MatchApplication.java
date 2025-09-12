package com.matchday.match.domain;

import com.matchday.global.entity.BaseEntity;
import com.matchday.match.domain.enums.MatchApplicationStatus;
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

    @Column(name = "message", length = 200)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchApplicationStatus status;

    private LocalDateTime processedAt;

    public static MatchApplication createApplication(Match match, Team applicantTeam, String message) {
        MatchApplication application = new MatchApplication();

        application.match = match;
        application.applicantTeam = applicantTeam;
        application.message = message;
        application.status = MatchApplicationStatus.APPLIED;

        return application;
    }

    private void validateCanProcess() {
        if (this.status != MatchApplicationStatus.APPLIED) {
            throw new IllegalStateException("신청 상태에서만 처리할 수 있습니다.");
        }
    }

    public void accept() {
        validateCanProcess();
        this.status = MatchApplicationStatus.ACCEPTED;
        this.processedAt = LocalDateTime.now();
    }

    public void reject() {
        validateCanProcess();
        this.status = MatchApplicationStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != MatchApplicationStatus.APPLIED) {
            throw new IllegalStateException("신청 상태에서만 취소할 수 있습니다.");
        }
        this.status = MatchApplicationStatus.CANCELED;
        this.processedAt = LocalDateTime.now();
    }

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