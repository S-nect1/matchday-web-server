package com.matchday.auth.dto;

import lombok.Getter;

@Getter
public class RegisterResponse {
    private final Long userId;
    private final String email;
    private final String name;
    
    private RegisterResponse(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }
    
    public static RegisterResponse of(Long userId, String email, String name) {
        return new RegisterResponse(userId, email, name);
    }
}