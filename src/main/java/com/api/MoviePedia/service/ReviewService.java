package com.api.MoviePedia.service;

import com.api.MoviePedia.model.review.LikeDislikeDto;
import com.api.MoviePedia.model.review.ReviewCreationDto;
import com.api.MoviePedia.model.review.ReviewRetrievalDto;

public interface ReviewService {
    ReviewRetrievalDto writeMovieReview(ReviewCreationDto reviewCreationDto);

    void deleteMovieReviewById(Long reviewId);

    void likeMovieReview(LikeDislikeDto likeDto);

    void dislikeMovieReview(LikeDislikeDto likeDislikeDto);
}
