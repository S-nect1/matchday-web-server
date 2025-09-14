package com.matchday.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType = "Bearer";
    private final Long userId;
    private final String email;
    private final String name;
    private final String role;
    
    private LoginResponse(String accessToken, String refreshToken, Long userId, String email, String name, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }
    
    public static LoginResponse of(String accessToken, String refreshToken, Long userId, String email, String name, String role) {
        return new LoginResponse(accessToken, refreshToken, userId, email, name, role);
    }
}