package com.api.MoviePedia.service;

import com.api.MoviePedia.model.DirectorCreationDto;
import com.api.MoviePedia.model.DirectorRetrievalDto;
import com.api.MoviePedia.repository.model.DirectorEntity;

import java.io.IOException;
import java.util.List;

public interface DirectorService {
    List<DirectorRetrievalDto> getAllDirectors();

    DirectorRetrievalDto getDirectorById(Long directorId);

    DirectorEntity getDirectorEntityById(Long directorId);

    DirectorRetrievalDto createDirector(DirectorCreationDto directorCreationDto) throws IOException;

    DirectorRetrievalDto editDirectorById(Long directorId, DirectorCreationDto directorCreationDto) throws IOException;

    void deleteDirectorById(Long directorId);
}
