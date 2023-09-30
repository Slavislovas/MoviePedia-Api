package com.api.MoviePedia.repository;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.model.UserRetrievalDto;
import com.api.MoviePedia.repository.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByRole(Role roleContentCurator);
}
