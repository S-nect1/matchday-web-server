package com.matchday.inquiry.domain;

import com.matchday.global.entity.BaseEntity;
import com.matchday.inquiry.domain.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity
@Table(name = "inquiry")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(columnDefinition = "TEXT")
    private String answer;

    public static Inquiry createInquiry(String title, String content, String rawPassword, PasswordEncoder passwordEncoder) {
        Inquiry inquiry = new Inquiry();
        inquiry.title = title;
        inquiry.content = content;
        inquiry.password = passwordEncoder.encode(rawPassword);
        inquiry.status = Status.PENDING;
        return inquiry;
    }

    public boolean matchesPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void addAnswer(String answerContent) {
        this.answer = answerContent;
        this.status = Status.ANSWERED;
    }

    public boolean hasAnswer() {
        return this.answer != null && !this.answer.trim().isEmpty();
    }
}
