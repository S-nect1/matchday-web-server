package com.matchday.auth.service;

import com.matchday.security.auth.auth.application.RefreshTokenService;
import com.matchday.security.utils.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenProvider 테스트")
class TokenProviderTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private TokenProvider tokenProvider;

    private Long userId;
    private String email;
    private String role;
    private String secretKey;
    private Long expiration;

    @BeforeEach
    void setUp() {
        userId = 1L;
        email = "test@example.com";
        role = "USER";
        secretKey = "mySecretKeyForJwtTokenGenerationAndValidation1234567890";
        expiration = 3600000L; // 1시간 (밀리초)
        
        ReflectionTestUtils.setField(tokenProvider, "secretKey", secretKey);
        ReflectionTestUtils.setField(tokenProvider, "expiration", expiration);
    }
    
    @Test
    @DisplayName("JWT 토큰을 정상적으로 생성한다")
    void generateToken_Success() {
        // given
        Long userId = 1L;
        String email = "test@example.com";
        String role = "USER";
        
        // when
        String token = tokenProvider.generateToken(userId, email, role);
        
        // then
        assertThat(token).isNotNull().isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // JWT 구조: header.payload.signature
    }
    
    @Test
    @DisplayName("JWT 토큰에서 사용자 ID를 정상적으로 추출한다")
    void extractUserId_Success() {
        // given
        Long expectedUserId = 1L;
        String email = "test@example.com";
        String role = "USER";
        String token = tokenProvider.generateToken(expectedUserId, email, role);
        
        // when
        Long actualUserId = tokenProvider.extractUserId(token);
        
        // then
        assertThat(actualUserId).isEqualTo(expectedUserId);
    }
    
    @Test
    @DisplayName("JWT 토큰에서 이메일을 정상적으로 추출한다")
    void extractEmail_Success() {
        // given
        Long userId = 1L;
        String expectedEmail = "test@example.com";
        String role = "USER";
        String token = tokenProvider.generateToken(userId, expectedEmail, role);
        
        // when
        String actualEmail = tokenProvider.extractEmail(token);
        
        // then
        assertThat(actualEmail).isEqualTo(expectedEmail);
    }
    
    @Test
    @DisplayName("JWT 토큰에서 역할을 정상적으로 추출한다")
    void extractRole_Success() {
        // given
        Long userId = 1L;
        String email = "test@example.com";
        String expectedRole = "USER";
        String token = tokenProvider.generateToken(userId, email, expectedRole);
        
        // when
        String actualRole = tokenProvider.extractRole(token);
        
        // then
        assertThat(actualRole).isEqualTo(expectedRole);
    }
    
    @Test
    @DisplayName("유효한 JWT 토큰을 검증한다")
    void isTokenValid_Success() {
        // given
        Long userId = 1L;
        String email = "test@example.com";
        String role = "USER";
        String token = tokenProvider.generateToken(userId, email, role);
        
        // when
        boolean isValid = tokenProvider.isTokenValid(token);
        
        // then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("잘못된 JWT 토큰을 무효로 판단한다")
    void isTokenValid_InvalidToken() {
        // given
        String invalidToken = "invalid.jwt.token";
        
        // when
        boolean isValid = tokenProvider.isTokenValid(invalidToken);
        
        // then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("추가 클레임이 포함된 JWT 토큰을 생성한다")
    void generateTokenWithExtraClaims_Success() {
        // given
        Long userId = 1L;
        String email = "test@example.com";
        String role = "USER";
        Map<String, Object> extraClaims = Map.of("name", "Test User", "age", 25);
        
        // when
        String token = tokenProvider.generateToken(userId, email, role, extraClaims);
        
        // then
        assertThat(token).isNotNull().isNotBlank();
        assertThat(tokenProvider.extractUserId(token)).isEqualTo(userId);
        assertThat(tokenProvider.extractEmail(token)).isEqualTo(email);
        assertThat(tokenProvider.extractRole(token)).isEqualTo(role);
    }
    
    @Test
    @DisplayName("토큰 만료 여부를 정확히 판단한다")
    void isTokenExpired_Success() {
        // given
        Long userId = 1L;
        String email = "test@example.com";
        String role = "USER";
        String token = tokenProvider.generateToken(userId, email, role);
        
        // when
        boolean isExpired = tokenProvider.isTokenExpired(token);
        
        // then
        assertThat(isExpired).isFalse();
    }
}