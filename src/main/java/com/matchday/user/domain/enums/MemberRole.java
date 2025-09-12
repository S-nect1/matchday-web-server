package com.matchday.user.domain.enums;

public enum MemberRole {
    ROLE_ADMIN("관리자"),
    ROLE_MEMBER("일반회원");
    
    private final String description;
    
    MemberRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}