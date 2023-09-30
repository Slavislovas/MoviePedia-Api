package com.api.MoviePedia.service;

import com.api.MoviePedia.model.review.ReviewCreationDto;
import com.api.MoviePedia.model.review.ReviewRetrievalDto;

import java.util.Set;

public interface ReviewService {
    ReviewRetrievalDto writeMovieReview(Long directorId, Long movieId, ReviewCreationDto reviewCreationDto);

    void deleteMovieReviewById(Long directorId, Long movieId, Long reviewId);

    void likeMovieReview(Long directorId, Long movieId, Long reviewId);

    void dislikeMovieReview(Long directorId, Long movieId, Long reviewId);

    Set<ReviewRetrievalDto> getAllReviewsByDirectorIdAndMovieId(Long directorId, Long movieId);

    ReviewRetrievalDto getReviewById(Long directorId, Long movieId, Long reviewId);

    ReviewRetrievalDto editReviewById(Long reviewId, Long movieId, Long directorId, ReviewCreationDto creationDto);
}
