package com.api.MoviePedia.controller;

import com.api.MoviePedia.exception.RequestBodyFieldValidationException;
import com.api.MoviePedia.model.FieldValidationErrorModel;
import com.api.MoviePedia.model.MovieCreationDto;
import com.api.MoviePedia.model.MovieRetrievalDto;
import com.api.MoviePedia.model.SearchDto;
import com.api.MoviePedia.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

@RequestMapping("/movie")
@RequiredArgsConstructor
@RestController
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/get/all")
    public ResponseEntity<List<MovieRetrievalDto>> getAllMovies(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MovieRetrievalDto> getMovieById(@PathVariable("id") Long movieId){
        return new ResponseEntity<>(movieService.getMovieById(movieId), HttpStatus.FOUND);
    }

    @PostMapping("/search")
    public ResponseEntity<List<MovieRetrievalDto>> getMoviesBySearchCriteria(@RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum,
                                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                             @RequestBody SearchDto searchDto){
        return ResponseEntity.ok(movieService.getMoviesBySearchCriteria(searchDto, pageNum, pageSize));
    }

    @PostMapping("/create")
    public ResponseEntity<MovieRetrievalDto> createMovie(@RequestBody @Valid MovieCreationDto movieCreationDto, BindingResult bindingResult) throws IOException {
        validateRequestBodyFields(bindingResult);
        return new ResponseEntity<>(movieService.createMovie(movieCreationDto), HttpStatus.CREATED);
    }

    @PostMapping("/add/watched_movies/{id}")
    public ResponseEntity<Void> addMovieToLoggedInUserWatchedMoviesById(@PathVariable("id") Long movieId){
        movieService.addMovieToLoggedInUserWatchedMoviesById(movieId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add/watchlist/{id}")
    public ResponseEntity<Void> addMovieToLoggedInUserWatchlistById(@PathVariable("id") Long movieId){
        movieService.addMovieToLoggedInUserWatchlistById(movieId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<MovieRetrievalDto> editMovieById(@PathVariable("id") Long movieId, @RequestBody @Valid MovieCreationDto movieCreationDto,
                                                           BindingResult bindingResult) throws IOException {
        validateRequestBodyFields(bindingResult);
        return ResponseEntity.ok(movieService.editMovieById(movieId, movieCreationDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMovieById(@PathVariable("id") Long movieId){
        movieService.deleteMovieById(movieId);
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
