package com.api.MoviePedia.controller;

import com.api.MoviePedia.exception.RequestBodyFieldValidationException;
import com.api.MoviePedia.model.FieldValidationErrorModel;
import com.api.MoviePedia.model.review.ReviewCreationDto;
import com.api.MoviePedia.model.review.ReviewRetrievalDto;
import com.api.MoviePedia.service.ReviewService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/directors/{directorId}/movies/{movieId}/reviews")
    public ResponseEntity<Set<ReviewRetrievalDto>> getAllReviewsByDirectorIdAndMovieId(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId){
        return ResponseEntity.ok(reviewService.getAllReviewsByDirectorIdAndMovieId(directorId, movieId));
    }

    @GetMapping("/directors/{directorId}/movies/{movieId}/reviews/{reviewId}")
    public ResponseEntity<ReviewRetrievalDto> getReviewById(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, @PathVariable("reviewId") Long reviewId){
        return ResponseEntity.ok(reviewService.getReviewById(directorId, movieId, reviewId));
    }

    @PostMapping("/directors/{directorId}/movies/{movieId}/reviews")
    public ResponseEntity<ReviewRetrievalDto> createMovieReview(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId,
                                                                @RequestBody @Valid ReviewCreationDto reviewCreationDto, BindingResult bindingResult){
        validateRequestBodyFields(bindingResult);
        return new ResponseEntity<>(reviewService.writeMovieReview(directorId, movieId, reviewCreationDto), HttpStatus.CREATED);
    }

    @PostMapping("/directors/{directorId}/movies/{movieId}/reviews/{reviewId}/likes")
    public ResponseEntity<Void> likeMovieReview(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId,
                                                @PathVariable("reviewId") Long reviewId){
        reviewService.likeMovieReview(directorId, movieId, reviewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/directors/{directorId}/movies/{movieId}/reviews/{reviewId}/dislikes")
    public ResponseEntity<Void> dislikeMovieReview(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId,
                                                   @PathVariable("reviewId") Long reviewId){
        reviewService.dislikeMovieReview(directorId, movieId, reviewId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/directors/{directorId}/movies/{movieId}/reviews/{reviewId}")
    public ResponseEntity<ReviewRetrievalDto> editReviewById(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId,
                                                             @PathVariable("directorId") Long directorId, @RequestBody @Valid ReviewCreationDto creationDto, BindingResult bindingResult){
        validateRequestBodyFields(bindingResult);
        return ResponseEntity.ok(reviewService.editReviewById(reviewId, movieId, directorId, creationDto));
    }

    @DeleteMapping("/directors/{directorId}/movies/{movieId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteMovieReviewById(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, @PathVariable("reviewId") Long reviewId){
        reviewService.deleteMovieReviewById(directorId, movieId, reviewId);
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
