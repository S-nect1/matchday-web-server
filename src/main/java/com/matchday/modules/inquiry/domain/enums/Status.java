package com.matchday.modules.inquiry.domain.enums;

public enum Status {
    PENDING("대기중"),
    ANSWERED("답변완료");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
