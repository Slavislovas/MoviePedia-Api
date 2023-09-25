package com.api.MoviePedia.service;

import com.api.MoviePedia.model.review.LikeDislikeDto;
import com.api.MoviePedia.model.review.ReviewCreationDto;
import com.api.MoviePedia.model.review.ReviewRetrievalDto;

import java.util.List;
import java.util.Set;

public interface ReviewService {
    ReviewRetrievalDto writeMovieReview(ReviewCreationDto reviewCreationDto);

    void deleteMovieReviewById(Long reviewId);

    void likeMovieReview(LikeDislikeDto likeDto);

    void dislikeMovieReview(LikeDislikeDto likeDislikeDto);

    Set<ReviewRetrievalDto> getAllReviewsByMovieId(Long movieId);
}
