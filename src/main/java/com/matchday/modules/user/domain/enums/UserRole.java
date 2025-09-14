package com.matchday.modules.user.domain.enums;

public enum UserRole {
    ROLE_ADMIN("관리자"),
    ROLE_MEMBER("일반회원");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}