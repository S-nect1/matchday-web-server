package com.matchday.modules.match.domain.enums;

public enum MatchStatus {
    PENDING("대기중"),
    CONFIRMED("확정됨"),
    COMPLETED("완료됨"),
    CANCELED("취소됨");

    private final String description;

    MatchStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}