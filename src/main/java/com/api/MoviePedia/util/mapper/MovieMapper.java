package com.api.MoviePedia.util.mapper;

import com.api.MoviePedia.model.movie.MovieCreationDto;
import com.api.MoviePedia.model.movie.MovieRetrievalDto;
import com.api.MoviePedia.repository.model.ActorEntity;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.ImgurImageEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.repository.model.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {ActorMapper.class, DirectorMapper.class})
public abstract class MovieMapper {

    @Mapping(target = "pictureFilePath", expression = "java(imgurImageLink)")
    public abstract MovieRetrievalDto entityToRetrievalDto(MovieEntity entity, String imgurImageLink);

    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "totalRating", expression = "java(totalRating)")
    @Mapping(target = "imgurImageEntity", expression = "java(imgurImageEntity)")
    @Mapping(target = "totalVotes", expression = "java(totalVotes)")
    @Mapping(target = "rating", expression = "java(rating)")
    @Mapping(target = "director", expression = "java(director)")
    @Mapping(target = "actors", expression = "java(actors)")
    @Mapping(target = "reviews", expression = "java(reviews)")
    public abstract MovieEntity creationDtoToEntity(MovieCreationDto movieCreationDto, Long id, ImgurImageEntity imgurImageEntity,
                                                    Integer totalRating, Integer totalVotes, Double rating,
                                                    DirectorEntity director, Set<ActorEntity> actors, Set<ReviewEntity> reviews);
}
