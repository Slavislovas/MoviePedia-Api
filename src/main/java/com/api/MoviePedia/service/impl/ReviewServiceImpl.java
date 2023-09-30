package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.model.review.ReviewCreationDto;
import com.api.MoviePedia.model.review.ReviewRetrievalDto;
import com.api.MoviePedia.repository.DislikeRepository;
import com.api.MoviePedia.repository.LikeRepository;
import com.api.MoviePedia.repository.ReviewRepository;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.DislikeEntity;
import com.api.MoviePedia.repository.model.LikeEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.repository.model.ReviewEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.DirectorService;
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
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final DirectorService directorService;

    @Override
    public Set<ReviewRetrievalDto> getAllReviewsByDirectorIdAndMovieId(Long directorId, Long movieId) {
        DirectorEntity directorEntity = directorService.getDirectorEntityById(directorId);
        Set<ReviewEntity> reviewEntities = directorEntity
                .getMovies()
                .stream()
                .filter(movieEntity -> movieEntity.getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Director has not made a movie with id: " + movieId))
                .getReviews();
        return reviewEntities.stream()
                .map(reviewEntity -> reviewMapper.entityToRetrievalDto(reviewEntity, reviewEntity.getLikes().size(), reviewEntity.getDislikes().size())).collect(Collectors.toSet());
    }

    @Override
    public ReviewRetrievalDto getReviewById(Long directorId, Long movieId, Long reviewId) {
        DirectorEntity directorEntity = directorService.getDirectorEntityById(directorId);
        ReviewEntity reviewEntity = directorEntity
                .getMovies()
                .stream()
                .filter(movieEntity -> movieEntity.getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Director has not made a movie with id: " + movieId))
                .getReviews()
                .stream()
                .filter(review -> review.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Review with id: " + reviewId + " does not exist"));
        return reviewMapper.entityToRetrievalDto(reviewEntity, reviewEntity.getLikes().size(), reviewEntity.getDislikes().size());
    }

    @Override
    public ReviewRetrievalDto editReviewById(Long reviewId, Long movieId, Long directorId, ReviewCreationDto creationDto) {
        DirectorEntity directorEntity = directorService.getDirectorEntityById(directorId);
        ReviewEntity reviewEntity = directorEntity
                .getMovies()
                .stream()
                .filter(movieEntity -> movieEntity.getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Director has not made a movie with id: " + movieId))
                .getReviews()
                .stream()
                .filter(review -> review.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Review with id: " + reviewId + " does not exist"));
        validateUserPermissions(reviewEntity.getReviewer().getId(), "Users can only edit their own reviews");
        reviewEntity = reviewMapper.creationDtoToEntity(creationDto, reviewEntity.getId(), reviewEntity.getLikes(), reviewEntity.getDislikes(), reviewEntity.getReviewer(), reviewEntity.getMovie());
        return reviewMapper.entityToRetrievalDto(reviewRepository.save(reviewEntity), reviewEntity.getLikes().size(), reviewEntity.getDislikes().size());
    }

    @Override
    public ReviewRetrievalDto writeMovieReview(Long directorId, Long movieId, ReviewCreationDto reviewCreationDto) {
        DirectorEntity directorEntity = directorService.getDirectorEntityById(directorId);
        MovieEntity movieEntity = directorEntity
                .getMovies()
                .stream()
                .filter(filteredMovieEntity -> filteredMovieEntity.getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Director has not made a movie with id: " + movieId));
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity userEntity = userService.getUserEntityById(userId);
        if (reviewRepository.findByReviewerIdAndMovieId(userId, movieEntity.getId()).isPresent()) {
            throw new DuplicateDatabaseEntryException("You have already reviewed this movie");
        }
        ReviewEntity reviewEntity = reviewMapper.creationDtoToEntity(reviewCreationDto, null, new HashSet<>(), new HashSet<>(), userEntity, movieEntity);
        return reviewMapper.entityToRetrievalDto(reviewRepository.save(reviewEntity), reviewEntity.getLikes().size(), reviewEntity.getDislikes().size());
    }

    @Override
    public void deleteMovieReviewById(Long directorId, Long movieId, Long reviewId) {
        DirectorEntity directorEntity = directorService.getDirectorEntityById(directorId);
        ReviewEntity reviewEntity = directorEntity
                .getMovies()
                .stream()
                .filter(movieEntity -> movieEntity.getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Director has not made a movie with id: " + movieId))
                .getReviews()
                .stream()
                .filter(review -> review.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Review with id: " + reviewId + " does not exist"));
        validateUserPermissions(reviewEntity.getReviewer().getId(), "Users can only delete their own reviews");
        reviewRepository.deleteById(reviewId);
    }

    @Transactional
    @Override
    public void likeMovieReview(Long directorId, Long movieId, Long reviewId){
        DirectorEntity directorEntity = directorService.getDirectorEntityById(directorId);
        ReviewEntity reviewEntity = directorEntity
                .getMovies()
                .stream()
                .filter(movieEntity -> movieEntity.getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Director has not made a movie with id: " + movieId))
                .getReviews()
                .stream()
                .filter(review -> review.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Review with id: " + reviewId + " does not exist"));
        validateReviewLikeDislikePermissions(reviewEntity.getReviewer().getId(), "Users cannot like their own reviews");
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<LikeEntity> optionalLikeEntity = likeRepository.findByUserIdAndReviewId(userId, reviewId);
        if (optionalLikeEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("User with id: " + userId + " has already liked this review");
        }
        Optional<DislikeEntity> optionalDislikeEntity = dislikeRepository.findByUserIdAndReviewId(userId, reviewId);
        if (optionalDislikeEntity.isPresent()){
            dislikeRepository.deleteByUserIdAndReviewId(userId, reviewId);
        }
        UserEntity userEntity = userService.getUserEntityById(userId);
        LikeEntity likeEntity = new LikeEntity(null, userEntity, reviewEntity);
        likeRepository.save(likeEntity);
        reviewRepository.save(reviewEntity);
    }

    @Transactional
    @Override
    public void dislikeMovieReview(Long directorId, Long movieId, Long reviewId) {
        DirectorEntity directorEntity = directorService.getDirectorEntityById(directorId);
        ReviewEntity reviewEntity = directorEntity
                .getMovies()
                .stream()
                .filter(movieEntity -> movieEntity.getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Director has not made a movie with id: " + movieId))
                .getReviews()
                .stream()
                .filter(review -> review.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Review with id: " + reviewId + " does not exist"));
        validateReviewLikeDislikePermissions(reviewEntity.getReviewer().getId(), "Users cannot dislike their own reviews");
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<DislikeEntity> optionalDislikeEntity = dislikeRepository.findByUserIdAndReviewId(userId, reviewId);
        if (optionalDislikeEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("User with id: " + userId + " has already disliked this review");
        }
        Optional<LikeEntity> optionalLikeEntity = likeRepository.findByUserIdAndReviewId(userId, reviewId);
        if (optionalLikeEntity.isPresent()){
            likeRepository.deleteByUserIdAndReviewId(userId, reviewId);
        }
        UserEntity userEntity = userService.getUserEntityById(userId);
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
