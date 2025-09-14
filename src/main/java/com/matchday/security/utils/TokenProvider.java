package com.matchday.security.utils;

import com.matchday.security.auth.auth.domain.RefreshToken;
import com.matchday.security.auth.auth.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenProvider {
    
    private final RefreshTokenService refreshTokenService;
    
    @Value("${jwt.access-token.secret-key}")
    private String secretKey;
    
    @Value("${jwt.access-token.expire-time}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // jwt 생성
    public String generateToken(Long userId, String email, String role, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .claims(extraClaims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }
    
    public String generateToken(Long userId, String email, String role) {
        return generateToken(userId, email, role, Map.of());
    }

    // 클레임 및 필드 추출
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(extractClaims(token).getSubject());
    }
    
    public String extractEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }
    
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // 토큰 유효성 검증
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }


    /**
     * refresh token 관련
     */

    // refresh token 발급
    public RefreshToken createRefreshToken(Long userId) {
        return refreshTokenService.createRefreshToken(userId);
    }
    
    // refresh token rotate
    public RefreshToken rotateRefreshToken(String currentRefreshToken) {
        return refreshTokenService.rotateRefreshToken(currentRefreshToken);
    }
    
    // refresh token 검증
    public RefreshToken validateRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.findToken(refreshToken);
        
        if (!token.isValid()) {
            throw new IllegalArgumentException("무효한 RefreshToken입니다.");
        }
        
        return token;
    }
}