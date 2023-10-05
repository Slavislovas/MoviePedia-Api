package com.api.MoviePedia.repository;

import com.api.MoviePedia.repository.model.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByUserId(Long userId);

    Optional<RefreshTokenEntity> findByToken(String refreshToken);
}
