package com.matchday.security.auth.auth.application;

import com.matchday.security.auth.auth.domain.RefreshToken;
import com.matchday.security.auth.auth.exception.AuthControllerAdvice;
import com.matchday.security.auth.auth.infrastructure.RefreshTokenRepository;
import com.matchday.common.entity.enums.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token.expire-time}")
    private Long refreshTokenExpireTime;

    private Long getTtlSeconds() {
        return refreshTokenExpireTime / 1000;
    }

    // 새로운 RefreshToken 생성
    public RefreshToken createRefreshToken(Long userId) {
        Long ttlSeconds = getTtlSeconds();

        RefreshToken refreshToken = RefreshToken.createToken(userId, ttlSeconds);

        // 현재 family의 RT 포인터 설정
        String familyCurrentKey = familyPointerKey(refreshToken.getFamilyId());
        redisTemplate.opsForValue()
                .set(familyCurrentKey, refreshToken.getToken(), ttlSeconds, java.util.concurrent.TimeUnit.SECONDS);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String currentToken) {
        RefreshToken currentRefreshToken = findToken(currentToken);
        final String key = familyPointerKey(currentRefreshToken.getFamilyId());
        final Long ttlSeconds = getTtlSeconds();

        guardReplayOrRevoked(currentRefreshToken);

        // 현재 기준으로 최신 RT가 아니라면 최신 RT 반환
        String ptr = getOrInitPointer(key, currentRefreshToken.getToken(), ttlSeconds);
        if (!currentRefreshToken.getToken().equals(ptr)) {
            return refreshTokenRepository.findById(ptr)
                    .orElseThrow(() -> new AuthControllerAdvice(ResponseCode.REFRESH_TOKEN_INVALID));
        }

        RefreshToken newRefreshToken = issueChildAndMarkParent(currentRefreshToken, ttlSeconds);

        redisTemplate.opsForValue()
                .set(key, newRefreshToken.getToken(), ttlSeconds, java.util.concurrent.TimeUnit.SECONDS);

        return newRefreshToken;
    }
    
    // RT 조회
    public RefreshToken findToken(String token) {
        return refreshTokenRepository.findById(token)
                .orElseThrow(() -> new AuthControllerAdvice(ResponseCode.REFRESH_TOKEN_INVALID));
    }

    // RT 재사용 감지
    private void guardReplayOrRevoked(RefreshToken current) {
        if (current.hasBeenReplaced()) {
            log.warn("토큰 재사용 감지 userId={}, familyId={}, token={}",
                    current.getUserId(), current.getFamilyId(), current.getToken());
            revokeFamily(current.getFamilyId());
            throw new AuthControllerAdvice(ResponseCode.REFRESH_TOKEN_INVALID);
        }

        if (!current.isValid()) {
            throw new AuthControllerAdvice(ResponseCode.REFRESH_TOKEN_INVALID);
        }
    }

    // 최신 RT 캐싱
    private String getOrInitPointer(String key, String currentToken, long ttl) {
        var ops = redisTemplate.opsForValue();
        String ptr = redisTemplate.opsForValue().get(key);
        if (ptr == null) {
            ops.setIfAbsent(key, currentToken, ttl, java.util.concurrent.TimeUnit.SECONDS);
            ptr = ops.get(key);
        }

        return ptr;
    }

    // rotation
    private RefreshToken issueChildAndMarkParent(RefreshToken parent, long ttl) {
        RefreshToken child = RefreshToken.rotateToken(parent, ttl);
        refreshTokenRepository.save(child);

        parent.revokeByRotation(child.getToken());
        refreshTokenRepository.save(parent);
        return child;
    }

    private String familyPointerKey(String familyId) {
        return "family:" + familyId + ":current";
    }

    // 동일 세션의 RT 전부 폐기
    private void revokeFamily(String familyId) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByFamilyId(familyId);
        for (RefreshToken t : tokens) {
            t.revoke();
        }
        refreshTokenRepository.saveAll(tokens);

        redisTemplate.delete(familyPointerKey(familyId));
    }
}