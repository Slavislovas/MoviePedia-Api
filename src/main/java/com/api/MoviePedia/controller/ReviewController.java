package com.api.MoviePedia.controller;

import com.api.MoviePedia.exception.RequestBodyFieldValidationException;
import com.api.MoviePedia.model.FieldValidationErrorModel;
import com.api.MoviePedia.model.review.LikeDislikeDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/review")
@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<ReviewRetrievalDto> createMovieReview(@RequestBody @Valid ReviewCreationDto reviewCreationDto, BindingResult bindingResult){
        validateRequestBodyFields(bindingResult);
        return new ResponseEntity<ReviewRetrievalDto>(reviewService.writeMovieReview(reviewCreationDto), HttpStatus.CREATED);
    }

    @PostMapping("/like")
    public ResponseEntity<Void> likeMovieReview(@RequestBody @Valid LikeDislikeDto likeDislikeTo, BindingResult bindingResult){
        validateRequestBodyFields(bindingResult);
        reviewService.likeMovieReview(likeDislikeTo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike")
    public ResponseEntity<Void> dislikeMovieReview(@RequestBody @Valid LikeDislikeDto likeDislikeDto, BindingResult bindingResult){
        validateRequestBodyFields(bindingResult);
        reviewService.dislikeMovieReview(likeDislikeDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMovieReviewById(@PathVariable("id") Long reviewId){
        reviewService.deleteMovieReviewById(reviewId);
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
