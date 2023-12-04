package com.api.MoviePedia.util.mapper;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.model.UserCreationDto;
import com.api.MoviePedia.model.UserEditDto;
import com.api.MoviePedia.model.UserRetrievalDto;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.repository.model.ReviewEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "role", expression = "java(role)")
    @Mapping(target = "watchlist", expression = "java(watchlist)")
    @Mapping(target = "watchedMovies", expression = "java(watchedMovies)")
    @Mapping(target = "reviews", expression = "java(reviews)")
    public abstract UserEntity creationDtoToEntity(UserCreationDto creationDto, Long id, Role role, Set<MovieEntity> watchlist, Set<MovieEntity> watchedMovies, Set<ReviewEntity> reviews);

    public abstract UserRetrievalDto entityToRetrievalDto(UserEntity entity);

    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "username", expression = "java(username)")
    @Mapping(target = "password", expression = "java(password)")
    @Mapping(target = "watchlist", expression = "java(watchlist)")
    @Mapping(target = "watchedMovies", expression = "java(watchedMovies)")
    @Mapping(target = "reviews", expression = "java(reviews)")
    public abstract UserEntity editDtoToEntity(UserEditDto editDto, Long id, String username, String password,
                                               Set<MovieEntity> watchlist, Set<MovieEntity> watchedMovies, Set<ReviewEntity> reviews);
}
