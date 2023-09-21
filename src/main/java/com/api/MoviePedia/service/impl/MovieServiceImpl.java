package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.builder.MovieSpecificationBuilder;
import com.api.MoviePedia.model.MovieCreationDto;
import com.api.MoviePedia.model.MovieRetrievalDto;
import com.api.MoviePedia.model.SearchCriteriaDto;
import com.api.MoviePedia.model.SearchDto;
import com.api.MoviePedia.repository.MovieRepository;
import com.api.MoviePedia.repository.model.ActorEntity;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.ActorService;
import com.api.MoviePedia.service.DirectorService;
import com.api.MoviePedia.service.FileStorageService;
import com.api.MoviePedia.service.MovieService;
import com.api.MoviePedia.util.ImageComparator;
import com.api.MoviePedia.util.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final FileStorageService fileStorageService;
    private final DirectorService directorService;
    private final ActorService actorService;

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

}
