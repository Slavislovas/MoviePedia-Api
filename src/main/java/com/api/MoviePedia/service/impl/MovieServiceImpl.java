package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.builder.MovieSpecificationBuilder;
import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.model.movie.MovieCreationDto;
import com.api.MoviePedia.model.movie.MovieRetrievalDto;
import com.api.MoviePedia.model.movie.RatingCreationDto;
import com.api.MoviePedia.model.movie.SearchCriteriaDto;
import com.api.MoviePedia.model.movie.SearchDto;
import com.api.MoviePedia.repository.MovieRepository;
import com.api.MoviePedia.repository.RatingRepository;
import com.api.MoviePedia.repository.model.ActorEntity;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.repository.model.RatingEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.ActorService;
import com.api.MoviePedia.service.DirectorService;
import com.api.MoviePedia.service.FileStorageService;
import com.api.MoviePedia.service.MovieService;
import com.api.MoviePedia.service.UserService;
import com.api.MoviePedia.util.ImageComparator;
import com.api.MoviePedia.util.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final MovieMapper movieMapper;
    private final FileStorageService fileStorageService;
    private final DirectorService directorService;
    private final ActorService actorService;
    private final UserService userService;

    @Override
    public List<MovieRetrievalDto> getAllMovies() {
        return movieRepository.findAll().stream().map(movieMapper::entityToRetrievalDto).collect(Collectors.toList());
    }

    @Override
    public MovieRetrievalDto getMovieById(Long movieId) {
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        if (optionalMovieEntity.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + movieId  + " does not exist");
        }
        return movieMapper.entityToRetrievalDto(optionalMovieEntity.get());
    }

    @Override
    public MovieEntity getMovieEntityById(Long movieId) {
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        if (optionalMovieEntity.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + movieId  + " does not exist");
        }
        return optionalMovieEntity.get();
    }

    @Override
    public MovieRetrievalDto createMovie(MovieCreationDto movieCreationDto) throws IOException {
        DirectorEntity directorEntity = directorService.getDirectorEntityById(movieCreationDto.getDirectorId());
        Set<ActorEntity> actorEntities = new HashSet<>();
        for (Long actorId : movieCreationDto.getActorIds()) {
            actorEntities.add(actorService.getActorEntityById(actorId));
        }
        String imageFilePath = fileStorageService.saveFile(movieCreationDto.getPicture(), UUID.randomUUID().toString(), ".png");
        MovieEntity movieEntity = movieMapper.creationDtoToEntity(movieCreationDto, null, imageFilePath, 0, 0, 0.0, directorEntity, actorEntities, new HashSet<>());
        return movieMapper.entityToRetrievalDto(movieRepository.save(movieEntity));
    }

    @Override
    public MovieRetrievalDto editMovieById(Long movieId, MovieCreationDto movieCreationDto) throws IOException {
        Optional<MovieEntity> optionalMovieEntityById = movieRepository.findById(movieId);
        if (optionalMovieEntityById.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + movieId  + " does not exist");
        }
        String imageFilePath = editMoviePicture(movieCreationDto, optionalMovieEntityById.get());
        DirectorEntity directorEntity = directorService.getDirectorEntityById(movieCreationDto.getDirectorId());
        Set<ActorEntity> actorEntities = new HashSet<>();
        for (Long actorId : movieCreationDto.getActorIds()) {
            actorEntities.add(actorService.getActorEntityById(actorId));
        }
        MovieEntity movieEntity = movieMapper.creationDtoToEntity(movieCreationDto, optionalMovieEntityById.get().getId(), imageFilePath, optionalMovieEntityById.get().getTotalRating(),
                optionalMovieEntityById.get().getTotalVotes(), optionalMovieEntityById.get().getRating(), directorEntity, actorEntities, optionalMovieEntityById.get().getReviews());
        return movieMapper.entityToRetrievalDto(movieRepository.save(movieEntity));
    }

    private String editMoviePicture(MovieCreationDto newMovieData, MovieEntity oldMovieData) throws IOException {
        Boolean picturesAreTheSame = ImageComparator.areImagesEqual(newMovieData.getPicture(), fileStorageService.retrieveFileContents(oldMovieData.getPictureFilePath()));
        if (!picturesAreTheSame){
            fileStorageService.rewriteFileContents(oldMovieData.getPictureFilePath(), newMovieData.getPicture());
            return oldMovieData.getPictureFilePath();
        }
        return oldMovieData.getPictureFilePath();
    }

    @Override
    public void deleteMovieById(Long movieId) {
        Optional<MovieEntity> optionalMovieEntityById = movieRepository.findById(movieId);
        if (optionalMovieEntityById.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + movieId  + " does not exist");
        }
        MovieEntity movieEntity = optionalMovieEntityById.get();
        removeActorsFromMovie(movieEntity);
        removeMovieFromWatchlists(movieEntity);
        removeMovieFromWatchedMovies(movieEntity);
        fileStorageService.deleteFileByPath(movieEntity.getPictureFilePath());
        movieRepository.deleteById(movieId);
    }

    private void removeActorsFromMovie(MovieEntity movieEntity) {
        for (Iterator<ActorEntity> iterator = movieEntity.getActors().iterator(); iterator.hasNext();){
            ActorEntity actorEntity = iterator.next();
            iterator.remove();
            actorEntity.getMovies().remove(movieEntity);
        }
    }

    private void removeMovieFromWatchlists(MovieEntity movieEntity) {
        for (Iterator<UserEntity> iterator = movieEntity.getUsersWithMovieInWatchlist().iterator(); iterator.hasNext();){
            UserEntity userEntity = iterator.next();
            iterator.remove();
            userEntity.getWatchlist().remove(movieEntity);
        }
    }

    private void removeMovieFromWatchedMovies(MovieEntity movieEntity){
        for (Iterator<UserEntity> iterator = movieEntity.getUsersWhoHaveWatchedMovie().iterator(); iterator.hasNext();){
            UserEntity userEntity = iterator.next();
            iterator.remove();
            userEntity.getWatchedMovies().remove(movieEntity);
        }
    }

    @Override
    public List<MovieRetrievalDto> getMoviesBySearchCriteria(SearchDto searchDto, Integer pageNumber, Integer pageSize) {
        MovieSpecificationBuilder movieSpecificationBuilder = new MovieSpecificationBuilder();
        List<SearchCriteriaDto> criteriaList = searchDto.getSearchCriteriaList();
        if (criteriaList != null){
            criteriaList.forEach(criteria -> {
                criteria.setDataOption(searchDto.getDataOption());
                movieSpecificationBuilder.with(criteria);
            });
        }
        Specification<MovieEntity> movieSpecification = movieSpecificationBuilder.build();
        Pageable page = PageRequest.of(pageNumber, pageSize, Sort.by("year").descending());
        Page<MovieEntity> moviePage = movieRepository.findAll(movieSpecification, page);
        return moviePage.toList().stream().map(movieMapper::entityToRetrievalDto).collect(Collectors.toList());
    }

    @Override
    public void addMovieToWatchedMovies(Long movieId, Long userId) {
       validateUserPermissions(userId, "Users can only add movies to their own watched movies list");
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        if (optionalMovieEntity.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + movieId  + " does not exist");
        }
        UserEntity userEntity = userService.getUserEntityById(userId);
        MovieEntity movieEntity = optionalMovieEntity.get();
        userEntity.getWatchedMovies().add(movieEntity);
        movieRepository.save(movieEntity);
    }

    @Override
    public void addMovieToWatchlist(Long movieId, Long userId) {
        validateUserPermissions(userId, "Users can only add movies to their own watchlist");
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        if (optionalMovieEntity.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + movieId  + " does not exist");
        }
        UserEntity userEntity = userService.getUserEntityById(userId);
        MovieEntity movieEntity = optionalMovieEntity.get();
        userEntity.getWatchlist().add(movieEntity);
        movieRepository.save(movieEntity);
    }

    @Override
    public void deleteMovieFromWatchedMovies(Long movieId, Long userId) {
        validateUserPermissions(userId, "Users can only delete movies from their own watched movies list");
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        if (optionalMovieEntity.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + movieId  + " does not exist");
        }
        UserEntity userEntity = userService.getUserEntityById(userId);
        MovieEntity movieEntity = optionalMovieEntity.get();
        userEntity.getWatchedMovies().remove(movieEntity);
        movieRepository.save(movieEntity);
    }

    @Override
    public void deleteMovieFromWatchlist(Long movieId, Long userId) {
        validateUserPermissions(userId, "Users can only delete movies from their own watchlist");
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        if (optionalMovieEntity.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + movieId  + " does not exist");
        }
        UserEntity userEntity = userService.getUserEntityById(userId);
        MovieEntity movieEntity = optionalMovieEntity.get();
        userEntity.getWatchlist().remove(movieEntity);
        movieRepository.save(movieEntity);
    }

    @Override
    public Set<MovieRetrievalDto> getWatchedMoviesByUserId(Long userId) {
        validateUserPermissions(userId, "Users can only view their own watched movies list");
        UserEntity userEntity = userService.getUserEntityById(userId);
        return userEntity.getWatchedMovies().stream().map(movieMapper::entityToRetrievalDto).collect(Collectors.toSet());
    }

    @Override
    public Set<MovieRetrievalDto> getWatchlistByUserId(Long userId) {
        validateUserPermissions(userId, "Users can only view their own watchlist");
        UserEntity userEntity = userService.getUserEntityById(userId);
        return userEntity.getWatchlist().stream().map(movieMapper::entityToRetrievalDto).collect(Collectors.toSet());
    }

    @Override
    public void rateMovieById(RatingCreationDto ratingCreationDto) {
        validateUserPermissions(ratingCreationDto.getUserId(), "Users cannot rate movies for other users");
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(ratingCreationDto.getMovieId());
        if (optionalMovieEntity.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + ratingCreationDto.getMovieId()  + " does not exist");
        }
        MovieEntity movieEntity = optionalMovieEntity.get();
        if(checkIfUserAlreadyRatedMovie(ratingCreationDto.getUserId(), movieEntity)){
            throw new DuplicateDatabaseEntryException("User with id: " + ratingCreationDto.getUserId() + " has already rated this movie");
        }
        movieEntity.rateMovie(ratingCreationDto.getRating());
        UserEntity userEntity = userService.getUserEntityById(ratingCreationDto.getUserId());
        RatingEntity ratingEntity = new RatingEntity(null, ratingCreationDto.getRating(), userEntity, movieEntity);
        movieRepository.save(movieEntity);
        ratingRepository.save(ratingEntity);

    }

    @Override
    public Integer getRatingByUserIdAndMovieId(Long userId, Long movieId) {
        validateUserPermissions(userId, "Users can only retrieve their own rating for the movie");
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        if (optionalMovieEntity.isEmpty()){
            throw new NoSuchElementException("Movie with id: " + movieId  + " does not exist");
        }
        MovieEntity movieEntity = optionalMovieEntity.get();
        for (RatingEntity rating : movieEntity.getRatings()) {
            if (rating.getUser().getId().equals(userId)){
                return rating.getRating();
            }
        }
        throw new NoSuchElementException("User with id: " + userId + " has not rated this movie");
    }

    private Boolean checkIfUserAlreadyRatedMovie(Long userId, MovieEntity movieEntity) {
        for (RatingEntity ratingEntity : movieEntity.getRatings()) {
            if (ratingEntity.getUser().getId().equals(userId)){
                return true;
            }
        }
        return false;
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
