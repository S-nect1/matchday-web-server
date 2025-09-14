package com.matchday.security.auth.auth.controller;

import com.matchday.security.auth.auth.dto.request.LoginRequest;
import com.matchday.security.auth.auth.dto.response.LoginResponse;
import com.matchday.security.auth.auth.dto.request.RefreshTokenRequest;
import com.matchday.security.auth.auth.dto.response.RefreshTokenResponse;
import com.matchday.security.auth.auth.dto.request.RegisterRequest;
import com.matchday.security.auth.auth.dto.response.RegisterResponse;
import com.matchday.security.auth.auth.service.AuthService;
import com.matchday.common.entity.BaseResponse;
import com.matchday.common.entity.enums.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return BaseResponse.onSuccess(response, ResponseCode.LOGIN_SUCCESS);
    }
    
    @PostMapping("/refresh")
    public BaseResponse<RefreshTokenResponse> generateRefreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.generateRefreshToken(request);
        return BaseResponse.onSuccess(response, ResponseCode.TOKEN_REFRESH_SUCCESS);
    }
    
    @PostMapping("/register")
    public BaseResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return BaseResponse.onSuccess(response, ResponseCode.REGISTER_SUCCESS);
    }
}