package com.api.MoviePedia.repository;

import com.api.MoviePedia.repository.model.ContentCuratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentCuratorRepository extends JpaRepository<ContentCuratorEntity, Long> {
    Optional<ContentCuratorEntity> findByUsername(String username);

    Optional<ContentCuratorEntity> findByEmail(String email);
}
