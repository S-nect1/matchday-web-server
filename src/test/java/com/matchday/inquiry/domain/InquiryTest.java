package com.matchday.inquiry.domain;

import com.matchday.inquiry.domain.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

class InquiryTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("문의 생성 시 비밀번호가 암호화되어 저장된다")
    void createInquiry_ShouldEncodePassword() {
        // given
        String title = "문의 제목";
        String content = "문의 내용입니다.";
        String rawPassword = "1234";

        // when
        Inquiry inquiry = Inquiry.createInquiry(title, content, rawPassword, passwordEncoder);

        // then
        assertThat(inquiry.getTitle()).isEqualTo(title);
        assertThat(inquiry.getContent()).isEqualTo(content);
        assertThat(inquiry.getPasswordHash()).isNotEqualTo(rawPassword); // 암호화됨
        assertThat(inquiry.getPasswordHash()).startsWith("$2a$"); // BCrypt 해시
        assertThat(inquiry.getStatus()).isEqualTo(Status.PENDING);
        assertThat(inquiry.hasAnswer()).isFalse();
    }

    @Test
    @DisplayName("올바른 비밀번호로 문의 확인이 가능하다")
    void matchesPassword_WithCorrectPassword_ShouldReturnTrue() {
        // given
        String rawPassword = "1234";
        Inquiry inquiry = Inquiry.createInquiry("제목", "내용", rawPassword, passwordEncoder);

        // when
        boolean matches = inquiry.matchesPassword(rawPassword, passwordEncoder);

        // then
        assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("잘못된 비밀번호로 문의 확인이 불가능하다")
    void matchesPassword_WithWrongPassword_ShouldReturnFalse() {
        // given
        String rawPassword = "1234";
        String wrongPassword = "5678";
        Inquiry inquiry = Inquiry.createInquiry("제목", "내용", rawPassword, passwordEncoder);

        // when
        boolean matches = inquiry.matchesPassword(wrongPassword, passwordEncoder);

        // then
        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("관리자가 답변을 추가하면 상태가 ANSWERED로 변경된다")
    void addAnswer_ShouldChangeStatusToAnswered() {
        // given
        Inquiry inquiry = Inquiry.createInquiry("제목", "내용", "1234", passwordEncoder);
        String answerContent = "문의에 대한 답변입니다.";

        // when
        inquiry.addAnswer(answerContent);

        // then
        assertThat(inquiry.getAnswer()).isEqualTo(answerContent);
        assertThat(inquiry.getStatus()).isEqualTo(Status.ANSWERED);
        assertThat(inquiry.hasAnswer()).isTrue();
    }

    @Test
    @DisplayName("답변이 없으면 hasAnswer는 false를 반환한다")
    void hasAnswer_WithNoAnswer_ShouldReturnFalse() {
        // given
        Inquiry inquiry = Inquiry.createInquiry("제목", "내용", "1234", passwordEncoder);

        // when & then
        assertThat(inquiry.hasAnswer()).isFalse();
    }

    @Test
    @DisplayName("빈 답변이면 hasAnswer는 false를 반환한다")
    void hasAnswer_WithEmptyAnswer_ShouldReturnFalse() {
        // given
        Inquiry inquiry = Inquiry.createInquiry("제목", "내용", "1234", passwordEncoder);
        inquiry.addAnswer("   "); // 공백만 있는 답변

        // when & then
        assertThat(inquiry.hasAnswer()).isFalse();
    }
}