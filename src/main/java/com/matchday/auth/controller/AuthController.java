package com.matchday.auth.controller;

import com.matchday.auth.dto.LoginRequest;
import com.matchday.auth.dto.LoginResponse;
import com.matchday.auth.dto.RefreshTokenRequest;
import com.matchday.auth.dto.RefreshTokenResponse;
import com.matchday.auth.dto.RegisterRequest;
import com.matchday.auth.dto.RegisterResponse;
import com.matchday.auth.service.AuthService;
import com.matchday.global.entity.BaseResponse;
import com.matchday.global.entity.enums.ResponseCode;
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