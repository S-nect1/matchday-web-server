package com.matchday.security.auth.auth.controller;

import com.matchday.security.auth.auth.dto.request.LoginRequest;
import com.matchday.security.auth.auth.dto.response.LoginResponse;
import com.matchday.security.auth.auth.dto.request.RefreshTokenRequest;
import com.matchday.security.auth.auth.dto.response.RefreshTokenResponse;
import com.matchday.security.auth.auth.dto.request.RegisterRequest;
import com.matchday.security.auth.auth.dto.response.RegisterResponse;
import com.matchday.common.entity.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthControllerDocs {
    
    @Operation(
        summary = "사용자 로그인",
        description = "이메일과 비밀번호를 통한 사용자 로그인"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (이메일 없음, 비밀번호 불일치 등)",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        )
    })
    BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request);
    
    @Operation(
        summary = "토큰 갱신",
        description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "토큰 갱신 성공",
            content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "유효하지 않은 Refresh Token",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        )
    })
    BaseResponse<RefreshTokenResponse> generateRefreshToken(
        @Parameter(description = "Refresh Token 요청 정보", required = true)
        @Valid @RequestBody RefreshTokenRequest request
    );
    
    @Operation(
        summary = "회원가입",
        description = "사용자 회원가입을 진행합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = RegisterResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (이메일 중복, 유효성 검사 실패 등)",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        )
    })
    BaseResponse<RegisterResponse> register(
        @Parameter(description = "회원가입 요청 정보", required = true)
        @Valid @RequestBody RegisterRequest request
    );
}