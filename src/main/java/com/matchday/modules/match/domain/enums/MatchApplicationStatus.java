package com.matchday.modules.match.domain.enums;

public enum MatchApplicationStatus {
    APPLIED("신청함"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨"),
    CANCELED("취소됨");

    private final String description;

    MatchApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}