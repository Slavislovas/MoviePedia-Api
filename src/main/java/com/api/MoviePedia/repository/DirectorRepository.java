package com.api.MoviePedia.repository;

import com.api.MoviePedia.repository.model.DirectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<DirectorEntity, Long> {
    Optional<DirectorEntity> findByNameAndSurname(String name, String surname);
}
