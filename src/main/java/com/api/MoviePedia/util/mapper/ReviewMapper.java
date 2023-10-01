package com.api.MoviePedia.util.mapper;

import com.api.MoviePedia.model.review.ReviewCreationDto;
import com.api.MoviePedia.model.review.ReviewRetrievalDto;
import com.api.MoviePedia.repository.model.DislikeEntity;
import com.api.MoviePedia.repository.model.LikeEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.repository.model.ReviewEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {
    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "likes", expression = "java(likes)")
    @Mapping(target = "dislikes", expression = "java(dislikes)")
    @Mapping(target = "reviewer", expression = "java(reviewer)")
    @Mapping(target = "movie", expression = "java(movie)")
    public abstract ReviewEntity creationDtoToEntity(ReviewCreationDto creationDto, Long id, Set<LikeEntity> likes, Set<DislikeEntity> dislikes, UserEntity reviewer, MovieEntity movie);

    @Mapping(target = "likes", expression = "java(likes)")
    @Mapping(target = "dislikes", expression = "java(dislikes)")
    @Mapping(target = "reviewerUsername", source = "entity.reviewer.username")
    public abstract ReviewRetrievalDto entityToRetrievalDto(ReviewEntity entity, Integer likes, Integer dislikes);
}
