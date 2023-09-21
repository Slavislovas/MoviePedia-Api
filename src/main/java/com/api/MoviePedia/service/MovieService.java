package com.api.MoviePedia.service;

import com.api.MoviePedia.model.MovieCreationDto;
import com.api.MoviePedia.model.MovieRetrievalDto;
import com.api.MoviePedia.model.SearchCriteriaDto;
import com.api.MoviePedia.model.SearchDto;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    List<MovieRetrievalDto> getAllMovies();

    MovieRetrievalDto getMovieById(Long movieId);

    MovieRetrievalDto createMovie(MovieCreationDto movieCreationDto) throws IOException;

    MovieRetrievalDto editMovieById(Long movieId, MovieCreationDto movieCreationDto) throws IOException;

    void deleteMovieById(Long movieId);

    List<MovieRetrievalDto> getMoviesBySearchCriteria(SearchDto searchDto, Integer pageNumber, Integer pageSize);
}
