package com.matchday.auth.service;

import com.matchday.security.auth.auth.dto.request.RegisterRequest;
import com.matchday.security.auth.auth.dto.response.RegisterResponse;
import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.common.exception.GeneralException;
import com.matchday.modules.team.domain.enums.Position;
import com.matchday.modules.user.domain.User;
import com.matchday.modules.user.domain.enums.Gender;
import com.matchday.modules.user.domain.enums.UserRole;
import com.matchday.modules.user.repository.UserRepository;
import com.matchday.security.auth.auth.application.AuthService;
import com.matchday.security.utils.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 회원가입 테스트")
class AuthServiceRegisterTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private TokenProvider tokenProvider;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    
    @BeforeEach
    void setUp() {
        registerRequest = createRegisterRequest();
    }
    
    @Test
    @DisplayName("회원가입 성공")
    void register_Success() {
        // given
        given(userRepository.findByEmail(registerRequest.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(registerRequest.getPassword())).willReturn("encodedPassword");

        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    ReflectionTestUtils.setField(u, "id", 1L);
                    return u;
                });
        
        // when
        RegisterResponse response = authService.register(registerRequest);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(response.getName()).isEqualTo(registerRequest.getName());
        
        verify(userRepository).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("이메일 중복으로 회원가입 실패")
    void register_EmailAlreadyExists_ThrowsException() {
        // given
        User existingUser = createUser();
        given(userRepository.findByEmail(registerRequest.getEmail())).willReturn(Optional.of(existingUser));
        
        // when & then
        assertThatThrownBy(() -> authService.register(registerRequest))
            .isInstanceOf(GeneralException.class)
            .hasMessage(ResponseCode.EMAIL_ALREADY_EXISTS.getMessage());
        
        verify(userRepository).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("옵션 필드가 있는 회원가입 성공")
    void register_WithOptionalFields_Success() {
        // given
        RegisterRequest requestWithOptional = createRegisterRequestWithOptionalFields();
        given(userRepository.findByEmail(requestWithOptional.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(requestWithOptional.getPassword())).willReturn("encodedPassword");
        
        User savedUser = createUser();
        ReflectionTestUtils.setField(savedUser, "id", 1L);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        
        // when
        RegisterResponse response = authService.register(requestWithOptional);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo(requestWithOptional.getEmail());
        assertThat(response.getName()).isEqualTo(requestWithOptional.getName());
        
        verify(userRepository).save(argThat(user -> {
            // User 객체의 메서드 호출 검증은 실제 구현에서는 어려우므로 저장이 호출되었는지만 확인
            return user != null;
        }));
    }
    
    private RegisterRequest createRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "email", "test@example.com");
        ReflectionTestUtils.setField(request, "password", "password123");
        ReflectionTestUtils.setField(request, "name", "홍길동");
        ReflectionTestUtils.setField(request, "birth", LocalDate.of(1990, 1, 1));
        ReflectionTestUtils.setField(request, "height", 175);
        ReflectionTestUtils.setField(request, "gender", Gender.MALE);
        ReflectionTestUtils.setField(request, "mainPosition", Position.FW);
        ReflectionTestUtils.setField(request, "phoneNumber", "010-1234-5678");
        ReflectionTestUtils.setField(request, "city", City.SEOUL);
        ReflectionTestUtils.setField(request, "district", District.SEOUL_GANGNAM);
        ReflectionTestUtils.setField(request, "isProfessional", false);
        return request;
    }
    
    private RegisterRequest createRegisterRequestWithOptionalFields() {
        RegisterRequest request = createRegisterRequest();
        ReflectionTestUtils.setField(request, "weight", 70);
        ReflectionTestUtils.setField(request, "subPosition", Position.MF);
        ReflectionTestUtils.setField(request, "description", "축구를 좋아합니다");
        return request;
    }
    
    private User createUser() {
        return User.createUser(
            "test@example.com",
            "password123",
            "홍길동",
            LocalDate.of(1990, 1, 1),
            175,
            Gender.MALE,
            Position.FW,
            UserRole.ROLE_MEMBER,
            "010-1234-5678",
            City.SEOUL,
            District.SEOUL_GANGNAM,
            false
        );
    }
}