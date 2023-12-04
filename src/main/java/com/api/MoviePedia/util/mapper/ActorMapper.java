package com.api.MoviePedia.util.mapper;

import com.api.MoviePedia.model.actor.ActorCreationDto;
import com.api.MoviePedia.model.actor.ActorRetrievalDto;
import com.api.MoviePedia.repository.model.ActorEntity;
import com.api.MoviePedia.repository.model.ImgurImageEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.service.FileStorageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class ActorMapper {
    @Autowired
    private FileStorageService fileStorageService;

    @Mapping(target = "imgurImageEntity", expression = "java(imgurImageEntity)")
    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "movies", expression = "java(movies)")
    public abstract ActorEntity creationDtoToEntity(ActorCreationDto creationDto, Long id, ImgurImageEntity imgurImageEntity, Set<MovieEntity> movies);

    @Mapping(target = "pictureFilePath", expression = "java(imgurImageLink)")
    public abstract ActorRetrievalDto entityToRetrievalDto(ActorEntity entity, String imgurImageLink);
}
