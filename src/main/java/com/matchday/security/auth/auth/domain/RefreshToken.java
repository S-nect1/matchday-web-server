package com.matchday.security.auth.auth.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("refresh_token")
public class RefreshToken {

    @Id
    private String token;

    @Indexed
    private Long userId;

    // 기기/브라우저 세션 식별자(로그인 시 생성, 회전 시 유지)
    @Indexed
    private String familyId;

    private String replacedByToken;

    private Boolean isRevoked;

    @TimeToLive
    private Long timeToLive;

    private RefreshToken(Long userId, String familyId, Long timeToLiveSeconds) {
        this.token = generateToken();
        this.userId = userId;
        this.familyId = familyId;
        this.replacedByToken = null;
        this.isRevoked = false;
        this.timeToLive = timeToLiveSeconds;
    }

    private RefreshToken(Long userId, Long timeToLiveSeconds) {
        this(userId, generateFamilyId(), timeToLiveSeconds);
    }

    public static RefreshToken createToken(Long userId, Long timeToLiveSeconds) {
        return new RefreshToken(userId, timeToLiveSeconds);
    }

    public static RefreshToken rotateToken(RefreshToken parent, Long timeToLiveSeconds) {
        return new RefreshToken(parent.getUserId(), parent.getFamilyId(), timeToLiveSeconds);
    }

    private String generateToken() {
        return "rt_" + UUID.randomUUID().toString().replace("-", "");
    }

    private static String generateFamilyId() {
        return "f_" + UUID.randomUUID().toString();
    }

    public void revoke() {
        this.isRevoked = true;
        this.replacedByToken = null;
    }

    // rotation
    public void revokeByRotation(String childToken) {
        this.isRevoked = true;
        this.replacedByToken = childToken;
    }

    // 회전된 토큰인지 (재사용 판정에 사용)
    public boolean hasBeenReplaced() {
        return this.isRevoked && this.replacedByToken != null;
    }

    public boolean isValid() {
        return !this.isRevoked;
    }
}