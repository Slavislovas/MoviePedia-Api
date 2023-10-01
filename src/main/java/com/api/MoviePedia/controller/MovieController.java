package com.api.MoviePedia.controller;

import com.api.MoviePedia.exception.RequestBodyFieldValidationException;
import com.api.MoviePedia.model.FieldValidationErrorModel;
import com.api.MoviePedia.model.movie.MovieCreationDto;
import com.api.MoviePedia.model.movie.MovieRetrievalDto;
import com.api.MoviePedia.model.movie.SearchDto;
import com.api.MoviePedia.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/movies")
    public ResponseEntity<List<MovieRetrievalDto>> getAllMovies(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/directors/{directorId}/movies")
    public ResponseEntity<Set<MovieRetrievalDto>> getAllMoviesByDirectorId(@PathVariable("directorId") Long directorId){
        return ResponseEntity.ok(movieService.getAllMoviesByDirectorId(directorId));
    }

    @GetMapping("/directors/{directorId}/movies/{movieId}")
    public ResponseEntity<MovieRetrievalDto> getDirectorMovieById(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId){
        return ResponseEntity.ok(movieService.getMovieById(directorId, movieId));
    }

    @GetMapping("/movies/watched")
    public ResponseEntity<Set<MovieRetrievalDto>> getWatchedMoviesByUserId(){
        return ResponseEntity.ok(movieService.getWatchedMoviesByUserId());
    }

    @GetMapping("/movies/watchlist")
    public ResponseEntity<Set<MovieRetrievalDto>> getWatchlistByUserId(){
        return ResponseEntity.ok(movieService.getWatchlistByUserId());
    }

    @PostMapping("/movies/search")
    public ResponseEntity<List<MovieRetrievalDto>> getMoviesBySearchCriteria(@RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum,
                                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                             @RequestBody SearchDto searchDto){
        return ResponseEntity.ok(movieService.getMoviesBySearchCriteria(searchDto, pageNum, pageSize));
    }

    @PostMapping("/directors/{directorId}/movies")
    public ResponseEntity<MovieRetrievalDto> createMovie(@PathVariable("directorId") Long directorId, @RequestBody @Valid MovieCreationDto movieCreationDto, BindingResult bindingResult) throws IOException {
        validateRequestBodyFields(bindingResult);
        return new ResponseEntity<>(movieService.createMovie(directorId, movieCreationDto), HttpStatus.CREATED);
    }

    @PostMapping("/directors/{directorId}/movies/{movieId}/watched")
    public ResponseEntity<Void> addMovieToWatchedMovies(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, @PathVariable("userId") Long userId){
        movieService.addMovieToWatchedMovies(directorId, movieId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/directors/{directorId}/movies/{movieId}/watchlist")
    public ResponseEntity<Void> addMovieToWatchlist(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId){
        movieService.addMovieToWatchlist(directorId, movieId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/directors/{directorId}/movies/{movieId}/rate/{rating}")
    public ResponseEntity<Void> rateMovieById(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId,
                                               @PathVariable("rating") Integer rating){
        movieService.rateMovieById(directorId, movieId, rating);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/directors/{directorId}/movies/{movieId}")
    public ResponseEntity<MovieRetrievalDto> editMovieById(@PathVariable("movieId") Long movieId, @PathVariable("directorId") Long directorId, @RequestBody @Valid MovieCreationDto movieCreationDto, BindingResult bindingResult) throws IOException {
        validateRequestBodyFields(bindingResult);
        return ResponseEntity.ok(movieService.editMovieById(movieId, directorId, movieCreationDto));
    }

    @PatchMapping(value = {"/directors/{directorId}/movies/{movieId}/actors/{actorIds}", "/directors/{directorId}/movies/{movieId}/actors"})
    public ResponseEntity<MovieRetrievalDto> setMovieActors(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, @PathVariable(value = "actorIds", required = false) Set<Long> actorIds){
        return ResponseEntity.ok(movieService.setMovieActors(directorId, movieId, actorIds));
    }

    @DeleteMapping("/directors/{directorId}/movies/{movieId}")
    public ResponseEntity<Void> deleteMovieById(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId){
        movieService.deleteMovieById(directorId, movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/directors/{directorId}/movies/{movieId}/watched")
    public ResponseEntity<Void> deleteMovieFromWatchedMovies(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId){
        movieService.deleteMovieFromWatchedMovies(directorId, movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/directors/{directorId}/movies/{movieId}/watchlist")
    public ResponseEntity<Void> deleteMovieFromWatchlist(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId){
        movieService.deleteMovieFromWatchlist(directorId, movieId);
        return ResponseEntity.ok().build();
    }

    private void validateRequestBodyFields(BindingResult bindingResult) {
        List<FieldValidationErrorModel> fieldValidationErrors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldValidationErrors.add(new FieldValidationErrorModel(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        if (!fieldValidationErrors.isEmpty()){
            throw new RequestBodyFieldValidationException(fieldValidationErrors);
        }
    }
}
