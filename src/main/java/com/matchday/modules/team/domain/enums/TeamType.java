package com.matchday.modules.team.domain.enums;

public enum TeamType {
    SMALL_GROUP("소모임"),
    CLUB("동아리"),
    COMMUNITY("동호회");
    
    private final String description;
    
    TeamType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}