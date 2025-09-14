package com.matchday.auth.dto;

import lombok.Getter;

@Getter
public class RefreshTokenResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType = "Bearer";
    
    private RefreshTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    public static RefreshTokenResponse of(String accessToken, String refreshToken) {
        return new RefreshTokenResponse(accessToken, refreshToken);
    }
}