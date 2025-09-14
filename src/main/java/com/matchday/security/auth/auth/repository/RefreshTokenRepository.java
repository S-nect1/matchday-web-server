package com.matchday.security.auth.auth.repository;

import com.matchday.security.auth.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    // 사용자 ID로 모든 유효한 RefreshToken 조회
    List<RefreshToken> findByUserIdAndIsRevokedFalse(Long userId);

    List<RefreshToken> findAllByFamilyId(String familyId);
}