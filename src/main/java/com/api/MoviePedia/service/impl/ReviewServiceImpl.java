package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.model.review.LikeDislikeDto;
import com.api.MoviePedia.model.review.ReviewCreationDto;
import com.api.MoviePedia.model.review.ReviewRetrievalDto;
import com.api.MoviePedia.repository.DislikeRepository;
import com.api.MoviePedia.repository.LikeRepository;
import com.api.MoviePedia.repository.ReviewRepository;
import com.api.MoviePedia.repository.model.DislikeEntity;
import com.api.MoviePedia.repository.model.LikeEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.repository.model.ReviewEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.MovieService;
import com.api.MoviePedia.service.ReviewService;
import com.api.MoviePedia.service.UserService;
import com.api.MoviePedia.util.mapper.ReviewMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final MovieService movieService;

    @Override
    public ReviewRetrievalDto writeMovieReview(ReviewCreationDto reviewCreationDto) {
        validateUserPermissions(reviewCreationDto.getReviewerId(), "Users cannot write reviews for other users");
        Optional<ReviewEntity> optionalReviewEntity = reviewRepository.findByReviewerId(reviewCreationDto.getReviewerId());
        if (optionalReviewEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("User with id: " + reviewCreationDto.getReviewerId() + " has already written a review for this movie");
        }
        MovieEntity movieEntity = movieService.getMovieEntityById(reviewCreationDto.getMovieId());
        UserEntity userEntity = userService.getUserEntityById(reviewCreationDto.getReviewerId());
        ReviewEntity reviewEntity = reviewMapper.creationDtoToEntity(reviewCreationDto, null, new HashSet<>(), new HashSet<>(),userEntity, movieEntity);
        return reviewMapper.entityToRetrievalDto(reviewRepository.save(reviewEntity), reviewEntity.getLikes().size(), reviewEntity.getDislikes().size());
    }

    @Override
    public void deleteMovieReviewById(Long reviewId) {
        Optional<ReviewEntity> optionalReviewEntity = reviewRepository.findById(reviewId);
        if (optionalReviewEntity.isEmpty()){
            throw new NoSuchElementException("Review with id: " + reviewId + " does not exist");
        }
        validateUserPermissions(optionalReviewEntity.get().getReviewer().getId(), "Users can only delete their own reviews");
        reviewRepository.deleteById(reviewId);
    }

    @Transactional
    @Override
    public void likeMovieReview(LikeDislikeDto likeDislikeDto){
        Optional<ReviewEntity> optionalReviewEntity = reviewRepository.findById(likeDislikeDto.getReviewId());
        if (optionalReviewEntity.isEmpty()){
            throw new NoSuchElementException("Review with id: " + likeDislikeDto.getReviewId() + " does not exist");
        }
        validateReviewLikeDislikePermissions(optionalReviewEntity.get().getReviewer().getId(), "Users cannot like their own reviews");
        Optional<LikeEntity> optionalLikeEntity = likeRepository.findByUserIdAndReviewId(likeDislikeDto.getUserId(), likeDislikeDto.getReviewId());
        if (optionalLikeEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("User with id: " + likeDislikeDto.getUserId() + " has already liked this review");
        }
        validateUserPermissions(likeDislikeDto.getUserId(), "User cannot like reviews for other users");
        ReviewEntity reviewEntity = optionalReviewEntity.get();
        Optional<DislikeEntity> optionalDislikeEntity = dislikeRepository.findByUserIdAndReviewId(likeDislikeDto.getUserId(), likeDislikeDto.getReviewId());
        if (optionalDislikeEntity.isPresent()){
            dislikeRepository.deleteByUserIdAndReviewId(likeDislikeDto.getUserId(), likeDislikeDto.getReviewId());
        }
        UserEntity userEntity = userService.getUserEntityById(likeDislikeDto.getUserId());
        LikeEntity likeEntity = new LikeEntity(null, userEntity, reviewEntity);
        likeRepository.save(likeEntity);
        reviewRepository.save(reviewEntity);
    }

    @Transactional
    @Override
    public void dislikeMovieReview(LikeDislikeDto likeDislikeDto) {
        Optional<ReviewEntity> optionalReviewEntity = reviewRepository.findById(likeDislikeDto.getReviewId());
        if (optionalReviewEntity.isEmpty()){
            throw new NoSuchElementException("Review with id: " + likeDislikeDto.getReviewId() + " does not exist");
        }
        validateReviewLikeDislikePermissions(optionalReviewEntity.get().getReviewer().getId(), "Users cannot dislike their own reviews");
        Optional<DislikeEntity> optionalDislikeEntity = dislikeRepository.findByUserIdAndReviewId(likeDislikeDto.getUserId(), likeDislikeDto.getReviewId());
        if (optionalDislikeEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("User with id: " + likeDislikeDto.getUserId() + " has already disliked this review");
        }
        validateUserPermissions(likeDislikeDto.getUserId(), "User cannot dislike reviews for other users");
        ReviewEntity reviewEntity = optionalReviewEntity.get();
        Optional<LikeEntity> optionalLikeEntity = likeRepository.findByUserIdAndReviewId(likeDislikeDto.getUserId(), likeDislikeDto.getReviewId());
        if (optionalLikeEntity.isPresent()){
            likeRepository.deleteByUserIdAndReviewId(likeDislikeDto.getUserId(), likeDislikeDto.getReviewId());
        }
        UserEntity userEntity = userService.getUserEntityById(likeDislikeDto.getUserId());
        DislikeEntity dislikeEntity = new DislikeEntity(null, userEntity, reviewEntity);
        dislikeRepository.save(dislikeEntity);
        reviewRepository.save(reviewEntity);
    }

    private void validateReviewLikeDislikePermissions(Long userId, String errorMessage) {
        Role role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(x -> Role.valueOf(x.getAuthority())).toList().get(0);
        if (role == Role.ROLE_USER){
            Long authenticatedUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (Objects.equals(authenticatedUserId, userId)){
                throw new SecurityException(errorMessage);
            }
        }
    }

    private void validateUserPermissions(Long userId, String errorMessage) {
        Role role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(x -> Role.valueOf(x.getAuthority())).toList().get(0);
        if (role == Role.ROLE_USER){
            Long authenticatedUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!Objects.equals(authenticatedUserId, userId)){
                throw new SecurityException(errorMessage);
            }
        }
    }
}
