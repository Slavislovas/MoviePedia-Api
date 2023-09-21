package com.api.MoviePedia.repository;

import com.api.MoviePedia.repository.model.ActorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<ActorEntity, Long> {
    Optional<ActorEntity> findByNameAndSurname(String name, String surname);
}
