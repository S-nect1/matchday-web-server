package com.matchday.match.domain;

import com.matchday.match.domain.enums.MatchApplicationStatus;
import com.matchday.match.exception.advice.MatchControllerAdvice;
import com.matchday.team.domain.Team;
import com.matchday.team.domain.TeamFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MatchApplicationTest {

    @Test
    @DisplayName("매치 신청 생성 시 기본 상태는 APPLIED이다")
    void createApplication_ShouldHaveAppliedStatus() {
        // given
        Match match = MatchFixture.defaultMatch();
        Team applicantTeam = TeamFixture.applicantTeam();
        String message = "우리 팀과 경기해요!";

        // when
        MatchApplication application = MatchApplication.createApplication(match, applicantTeam, message);

        // then
        assertThat(application.getStatus()).isEqualTo(MatchApplicationStatus.APPLIED);
        assertThat(application.getMatch()).isEqualTo(match);
        assertThat(application.getApplicantTeam()).isEqualTo(applicantTeam);
        assertThat(application.getMessage()).isEqualTo(message);
        assertThat(application.isApplied()).isTrue();
    }

    @Test
    @DisplayName("신청을 수락하면 상태가 ACCEPTED로 변경된다")
    void accept_ShouldChangeStatusToAccepted() {
        // given
        Match match = MatchFixture.defaultMatch();
        Team applicantTeam = TeamFixture.applicantTeam();
        MatchApplication application = MatchApplication.createApplication(match, applicantTeam, "메시지");

        // when
        application.accept();

        // then
        assertThat(application.getStatus()).isEqualTo(MatchApplicationStatus.ACCEPTED);
        assertThat(application.getProcessedAt()).isNotNull();
        assertThat(application.isAccepted()).isTrue();
    }

    @Test
    @DisplayName("신청을 거절하면 상태가 REJECTED로 변경된다")
    void reject_ShouldChangeStatusToRejected() {
        // given
        Match match = MatchFixture.defaultMatch();
        Team applicantTeam = TeamFixture.applicantTeam();
        MatchApplication application = MatchApplication.createApplication(match, applicantTeam, "메시지");

        // when
        application.reject();

        // then
        assertThat(application.getStatus()).isEqualTo(MatchApplicationStatus.REJECTED);
        assertThat(application.getProcessedAt()).isNotNull();
        assertThat(application.isRejected()).isTrue();
    }

    @Test
    @DisplayName("신청을 취소하면 상태가 CANCELED로 변경된다")
    void cancel_ShouldChangeStatusToCanceled() {
        // given
        Match match = MatchFixture.defaultMatch();
        Team applicantTeam = TeamFixture.applicantTeam();
        MatchApplication application = MatchApplication.createApplication(match, applicantTeam, "메시지");

        // when
        application.cancel();

        // then
        assertThat(application.getStatus()).isEqualTo(MatchApplicationStatus.CANCELED);
        assertThat(application.getProcessedAt()).isNotNull();
        assertThat(application.isCanceled()).isTrue();
    }

    @Test
    @DisplayName("이미 처리된 신청은 수락할 수 없다")
    void accept_AlreadyProcessedApplication_ShouldThrowException() {
        // given
        Match match = MatchFixture.defaultMatch();
        Team applicantTeam = TeamFixture.applicantTeam();
        MatchApplication application = MatchApplication.createApplication(match, applicantTeam, "메시지");
        application.accept(); // 이미 수락됨

        // when & then
        assertThatThrownBy(() -> application.accept())
            .isInstanceOf(MatchControllerAdvice.class);
    }

    @Test
    @DisplayName("이미 처리된 신청은 거절할 수 없다")
    void reject_AlreadyProcessedApplication_ShouldThrowException() {
        // given
        Match match = MatchFixture.defaultMatch();
        Team applicantTeam = TeamFixture.applicantTeam();
        MatchApplication application = MatchApplication.createApplication(match, applicantTeam, "메시지");
        application.reject(); // 이미 거절됨

        // when & then
        assertThatThrownBy(() -> application.reject())
            .isInstanceOf(MatchControllerAdvice.class);
    }

    @Test
    @DisplayName("처리된 신청은 취소할 수 없다")
    void cancel_ProcessedApplication_ShouldThrowException() {
        // given
        Match match = MatchFixture.defaultMatch();
        Team applicantTeam = TeamFixture.applicantTeam();
        MatchApplication application = MatchApplication.createApplication(match, applicantTeam, "메시지");
        application.accept(); // 이미 수락됨

        // when & then
        assertThatThrownBy(() -> application.cancel())
            .isInstanceOf(MatchControllerAdvice.class);
    }
}