package com.matchday.auth.service;

import com.matchday.security.auth.auth.domain.RefreshToken;
import com.matchday.security.auth.auth.exception.AuthControllerAdvice;
import com.matchday.security.auth.auth.repository.RefreshTokenRepository;
import com.matchday.security.auth.auth.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService 테스트")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private Long userId;
    private Long refreshTokenExpireTime;
    private String familyId;
    private String tokenValue;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        userId = 1L;
        refreshTokenExpireTime = 1209600000L; // 14일 (밀리초)
        familyId = "f_test-family-id";
        tokenValue = "rt_testtoken123";
        
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpireTime", refreshTokenExpireTime);
        
        refreshToken = RefreshToken.createToken(userId, refreshTokenExpireTime / 1000);
        ReflectionTestUtils.setField(refreshToken, "token", tokenValue);
        ReflectionTestUtils.setField(refreshToken, "familyId", familyId);
    }

    @Test
    @DisplayName("새로운 RefreshToken을 생성하고 Redis에 포인터를 설정한다")
    void createRefreshToken_Success() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(refreshToken);

        // when
        RefreshToken result = refreshTokenService.createRefreshToken(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getFamilyId()).isNotNull();
        
        // Redis 포인터 설정 검증
        then(valueOperations).should().set(
                anyString(), 
                anyString(), 
                eq(refreshTokenExpireTime / 1000), 
                eq(TimeUnit.SECONDS)
        );
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("RefreshToken을 회전하고 Redis 포인터를 업데이트한다")
    void rotateRefreshToken_Success() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        String familyKey = "family:" + familyId + ":current";
        long ttl = refreshTokenExpireTime / 1000;

        // 현재 RT 조회 & 포인터가 현재 RT를 가리키도록
        given(refreshTokenRepository.findById(tokenValue))
                .willReturn(Optional.of(refreshToken));
        given(valueOperations.get(familyKey))
                .willReturn(tokenValue);

        // save(...)가 받은 객체를 그대로 반환하도록 (부모/자식 모두)
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(inv -> inv.getArgument(0));

        // when
        RefreshToken result = refreshTokenService.rotateRefreshToken(tokenValue);

        // then
        ArgumentCaptor<RefreshToken> cap = ArgumentCaptor.forClass(RefreshToken.class);
        then(refreshTokenRepository).should(times(2)).save(cap.capture());

        List<RefreshToken> saved = cap.getAllValues();
        RefreshToken parentSaved = saved.stream()
                .filter(rt -> rt == refreshToken)
                .findFirst().orElseThrow();
        RefreshToken childSaved = saved.stream()
                .filter(rt -> rt != refreshToken)
                .findFirst().orElseThrow();

        // Redis 포인터가 자식 토큰으로 업데이트 되었는지 정확히 검증
        then(valueOperations).should().set(
                eq(familyKey),
                eq(childSaved.getToken()),
                eq(ttl),
                eq(TimeUnit.SECONDS)
        );
    }


    @Test
    @DisplayName("이미 회전된 토큰(재사용)으로 회전을 시도하면 전체 패밀리를 폐기한다")
    void rotateRefreshToken_TokenReplayDetection() {
        // given
        String familyKey = "family:" + familyId + ":current";
        refreshToken.revokeByRotation("rt_childtoken789");
        
        RefreshToken anotherTokenInFamily = RefreshToken.rotateToken(refreshToken, refreshTokenExpireTime / 1000);
        ReflectionTestUtils.setField(anotherTokenInFamily, "familyId", familyId);
        
        given(refreshTokenRepository.findById(tokenValue)).willReturn(Optional.of(refreshToken));
        given(refreshTokenRepository.findAllByFamilyId(familyId)).willReturn(List.of(refreshToken, anotherTokenInFamily));

        // when & then
        assertThatThrownBy(() -> refreshTokenService.rotateRefreshToken(tokenValue))
                .isInstanceOf(AuthControllerAdvice.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다.");
        
        // 전체 패밀리 폐기 검증
        then(refreshTokenRepository).should().saveAll(List.of(refreshToken, anotherTokenInFamily));
        then(redisTemplate).should().delete(familyKey);
    }

    @Test
    @DisplayName("현재 토큰이 최신이 아닌 경우 최신 토큰을 반환한다")
    void rotateRefreshToken_ReturnLatestToken() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        String familyKey = "family:" + familyId + ":current";
        String latestTokenValue = "rt_latesttoken999";
        RefreshToken latestToken = RefreshToken.rotateToken(refreshToken, refreshTokenExpireTime / 1000);
        ReflectionTestUtils.setField(latestToken, "token", latestTokenValue);
        ReflectionTestUtils.setField(latestToken, "familyId", familyId);
        
        given(refreshTokenRepository.findById(tokenValue)).willReturn(Optional.of(refreshToken));
        given(valueOperations.get(familyKey)).willReturn(latestTokenValue);
        given(refreshTokenRepository.findById(latestTokenValue)).willReturn(Optional.of(latestToken));

        // when
        RefreshToken result = refreshTokenService.rotateRefreshToken(tokenValue);

        // then
        assertThat(result).isEqualTo(latestToken);
        assertThat(result.getToken()).isEqualTo(latestTokenValue);
    }

    @Test
    @DisplayName("Redis 포인터가 없는 경우 현재 토큰으로 초기화한다")
    void rotateRefreshToken_InitializePointerWhenMissing() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        String familyKey = "family:" + familyId + ":current";
        long ttl = refreshTokenExpireTime / 1000;

        // ★ 실제 엔티티의 token 값을 테스트 기대값(tokenValue)과 일치시킨다
        ReflectionTestUtils.setField(refreshToken, "token", tokenValue);

        given(refreshTokenRepository.findById(tokenValue))
                .willReturn(Optional.of(refreshToken));

        // 포인터가 없을 때: 첫 get은 null, setIfAbsent 후 두 번째 get은 current token을 반환
        given(valueOperations.get(familyKey)).willReturn(null, tokenValue);

        // setIfAbsent를 정확 인자로 스텁
        given(valueOperations.setIfAbsent(
                eq(familyKey), eq(tokenValue), eq(ttl), eq(TimeUnit.SECONDS)))
                .willReturn(true);

        // save(...)는 받은 객체를 그대로 반환
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(inv -> inv.getArgument(0));

        // when
        RefreshToken result = refreshTokenService.rotateRefreshToken(tokenValue);

        // then
        assertThat(result).isNotNull();

        // 포인터 초기화가 setIfAbsent로 호출되었는지 검증
        then(valueOperations).should().setIfAbsent(
                eq(familyKey), eq(tokenValue), eq(ttl), eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("존재하지 않는 토큰으로 조회 시 예외가 발생한다")
    void findToken_TokenNotFound() {
        // given
        String nonExistentToken = "rt_nonexistent123";
        given(refreshTokenRepository.findById(nonExistentToken)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> refreshTokenService.findToken(nonExistentToken))
                .isInstanceOf(AuthControllerAdvice.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다.");
    }

    @Test
    @DisplayName("유효한 토큰을 조회할 수 있다")
    void findToken_Success() {
        // given
        given(refreshTokenRepository.findById(tokenValue)).willReturn(Optional.of(refreshToken));

        // when
        RefreshToken result = refreshTokenService.findToken(tokenValue);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(refreshToken);
        assertThat(result.getToken()).isEqualTo(tokenValue);
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("무효화된 토큰으로 회전을 시도하면 예외가 발생한다")
    void rotateRefreshToken_InvalidToken() {
        // given
        refreshToken.revoke(); // 토큰을 무효화
        
        given(refreshTokenRepository.findById(tokenValue)).willReturn(Optional.of(refreshToken));

        // when & then
        assertThatThrownBy(() -> refreshTokenService.rotateRefreshToken(tokenValue))
                .isInstanceOf(AuthControllerAdvice.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다.");
    }

    @Test
    @DisplayName("Redis 포인터에서 최신 토큰을 찾을 수 없는 경우 예외가 발생한다")
    void rotateRefreshToken_LatestTokenNotFound() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        String familyKey = "family:" + familyId + ":current";
        String latestTokenValue = "rt_latesttoken999";
        
        given(refreshTokenRepository.findById(tokenValue)).willReturn(Optional.of(refreshToken));
        given(valueOperations.get(familyKey)).willReturn(latestTokenValue);
        given(refreshTokenRepository.findById(latestTokenValue)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> refreshTokenService.rotateRefreshToken(tokenValue))
                .isInstanceOf(AuthControllerAdvice.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다.");
    }
}