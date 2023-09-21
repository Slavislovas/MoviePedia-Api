package com.api.MoviePedia.util.mapper;

import com.api.MoviePedia.model.ActorCreationDto;
import com.api.MoviePedia.model.ActorRetrievalDto;
import com.api.MoviePedia.repository.model.ActorEntity;
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

    @Mapping(target = "pictureFilePath", expression = "java(pictureFilePath)")
    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "movies", expression = "java(movies)")
    public abstract ActorEntity creationDtoToEntity(ActorCreationDto creationDto, Long id, String pictureFilePath, Set<MovieEntity> movies);
    public abstract ActorRetrievalDto entityToRetrievalDto(ActorEntity entity);
}
