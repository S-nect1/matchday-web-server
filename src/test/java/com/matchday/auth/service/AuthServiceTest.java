package com.matchday.auth.service;

import com.matchday.auth.dto.LoginRequest;
import com.matchday.auth.dto.LoginResponse;
import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.global.exception.GeneralException;
import com.matchday.user.domain.User;
import com.matchday.user.domain.enums.UserRole;
import com.matchday.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private TokenProvider tokenProvider;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() throws Exception {
        testUser = createTestUser();
        loginRequest = createLoginRequest();
    }
    
    private User createTestUser() throws Exception {
        User user = new User();
        
        // Reflection을 사용하여 private 필드 설정
        setField(user, "id", 1L);
        setField(user, "email", "test@example.com");
        setField(user, "password", "encodedPassword");
        setField(user, "name", "Test User");
        setField(user, "role", UserRole.ROLE_MEMBER);
        
        return user;
    }
    
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
    
    private LoginRequest createLoginRequest() {
        try {
            LoginRequest request = new LoginRequest();
            setField(request, "email", "test@example.com");
            setField(request, "password", "password123");
            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    @DisplayName("올바른 이메일과 비밀번호로 로그인에 성공한다")
    void login_Success() {
        // given
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(testUser));
        given(testUser.matchesPassword(loginRequest.getPassword(), passwordEncoder)).willReturn(true);
        given(tokenProvider.generateToken(any(), anyString(), anyString())).willReturn("test.jwt.token");
        
        // when
        LoginResponse response = authService.login(loginRequest);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("test.jwt.token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getName()).isEqualTo(testUser.getName());
        assertThat(response.getRole()).isEqualTo(testUser.getRole().name());
    }
    
    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
    void login_EmailNotFound() {
        // given
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("errorCode", ResponseCode.EMAIL_NOT_FOUND);
    }
    
    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
    void login_InvalidPassword() {
        // given
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(testUser));
        given(testUser.matchesPassword(loginRequest.getPassword(), passwordEncoder)).willReturn(false);
        
        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("errorCode", ResponseCode.INVALID_PASSWORD);
    }
}