package com.api.MoviePedia.service;

import com.api.MoviePedia.model.movie.MovieCreationDto;
import com.api.MoviePedia.model.movie.MovieRetrievalDto;
import com.api.MoviePedia.model.movie.SearchDto;
import com.api.MoviePedia.repository.model.MovieEntity;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface MovieService {
    List<MovieRetrievalDto> getAllMovies();

    MovieRetrievalDto getMovieById(Long directorId, Long movieId);

    MovieRetrievalDto createMovie(Long directorId, MovieCreationDto movieCreationDto) throws IOException;

    MovieRetrievalDto editMovieById(Long movieId, Long directorId, MovieCreationDto movieCreationDto) throws IOException;

    void deleteMovieById(Long directorId, Long movieId);

    List<MovieRetrievalDto> getMoviesBySearchCriteria(SearchDto searchDto, Integer pageNumber, Integer pageSize);

    void addMovieToWatchedMovies(Long directorId, Long movieId);

    void addMovieToWatchlist(Long directorId, Long movieId);

    void deleteMovieFromWatchedMovies(Long directorId, Long movieId);

    void deleteMovieFromWatchlist(Long directorId, Long movieId);

    Set<MovieRetrievalDto> getWatchedMoviesByUserId();

    Set<MovieRetrievalDto> getWatchlistByUserId();

    void rateMovieById(Long directorId, Long movieId, Integer rating);

    Integer getRatingByUserIdAndMovieId(Long userId, Long movieId);

    MovieEntity getMovieEntityById(Long movieId);

    Set<MovieRetrievalDto> getAllMoviesByDirectorId(Long directorId);

    MovieRetrievalDto setMovieActors(Long directorId, Long movieId, Set<Long> actorIds);
}
