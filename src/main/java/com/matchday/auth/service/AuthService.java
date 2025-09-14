package com.matchday.auth.service;

import com.matchday.auth.domain.RefreshToken;
import com.matchday.auth.dto.LoginRequest;
import com.matchday.auth.dto.LoginResponse;
import com.matchday.auth.dto.RefreshTokenRequest;
import com.matchday.auth.dto.RefreshTokenResponse;
import com.matchday.auth.dto.RegisterRequest;
import com.matchday.auth.dto.RegisterResponse;
import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.global.exception.GeneralException;
import com.matchday.user.domain.User;
import com.matchday.user.domain.enums.UserRole;
import com.matchday.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    public LoginResponse login(LoginRequest request) {
        // 이메일로 유저 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GeneralException(ResponseCode.EMAIL_NOT_FOUND));
        // 비밀번호 확인
        if (!user.matchesPassword(request.getPassword(), passwordEncoder)) {
            throw new GeneralException(ResponseCode.INVALID_PASSWORD);
        }

        // 토큰 발급
        String accessToken = tokenProvider.generateToken(
            user.getId(),
            user.getEmail(),
            user.getRole().name()
        );
        // refresh token 발급
        RefreshToken refreshToken = tokenProvider.createRefreshToken(user.getId());
        
        return LoginResponse.of(
            accessToken,
            refreshToken.getToken(),
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole().name()
        );
    }
    
    public RefreshTokenResponse generateRefreshToken(RefreshTokenRequest request) {
        RefreshToken oldRefreshToken = tokenProvider.validateRefreshToken(request.getRefreshToken());
        
        User user = userRepository.findById(oldRefreshToken.getUserId())
                .orElseThrow(() -> new GeneralException(ResponseCode.USER_NOT_FOUND));
        
        RefreshToken newRefreshToken = tokenProvider.rotateRefreshToken(request.getRefreshToken());
        
        String newAccessToken = tokenProvider.generateToken(
            user.getId(),
            user.getEmail(),
            user.getRole().name()
        );
        
        return RefreshTokenResponse.of(newAccessToken, newRefreshToken.getToken());
    }

    // TODO: 번호 인증
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GeneralException(ResponseCode.EMAIL_ALREADY_EXISTS);
        }
        
        User user = User.createUser(
            request.getEmail(),
            request.getPassword(),
            request.getName(),
            request.getBirth(),
            request.getHeight(),
            request.getGender(),
            request.getMainPosition(),
            UserRole.ROLE_MEMBER,
            request.getPhoneNumber(),
            request.getCity(),
            request.getDistrict(),
            request.getIsProfessional(),
            passwordEncoder
        );
        
        // 선택필드
        if (request.getWeight() != null) {
            user.updateWeight(request.getWeight());
        }
        if (request.getSubPosition() != null) {
            user.updateSubPosition(request.getSubPosition());
        }
        if (request.getDescription() != null) {
            user.updateDescription(request.getDescription());
        }
        
        User savedUser = userRepository.save(user);
        
        return RegisterResponse.of(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getName()
        );
    }
}