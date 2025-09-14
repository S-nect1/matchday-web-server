package com.matchday.modules.team.domain.enums;

public enum GroupGender {
    MALE("남성"),
    FEMALE("여성"),
    MIXED("혼성");
    
    private final String description;
    
    GroupGender(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}