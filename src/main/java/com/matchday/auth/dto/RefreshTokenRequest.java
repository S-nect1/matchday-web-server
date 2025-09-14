package com.matchday.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    
    @NotBlank(message = "RefreshToken은 필수입니다.")
    private String refreshToken;
    
    protected RefreshTokenRequest() {}
    
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public static RefreshTokenRequest of(String refreshToken) {
        return new RefreshTokenRequest(refreshToken);
    }
}