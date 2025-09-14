package com.matchday.auth.filter;

import lombok.Getter;

@Getter
public class JwtUserPrincipal {
    private final Long userId;
    private final String email;
    private final String role;
    
    private JwtUserPrincipal(Long userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
    
    public static JwtUserPrincipal of(Long userId, String email, String role) {
        return new JwtUserPrincipal(userId, email, role);
    }
}