package com.api.MoviePedia.util.mapper;

import com.api.MoviePedia.model.DirectorCreationDto;
import com.api.MoviePedia.model.DirectorRetrievalDto;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class DirectorMapper {
    public abstract DirectorRetrievalDto entityToRetrievalDto(DirectorEntity  entity);

    @Mapping(target = "pictureFilePath", expression = "java(pictureFilePath)")
    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "movies", expression = "java(movies)")
    public abstract DirectorEntity creationDtoToEntity(DirectorCreationDto directorCreationDto, Long id, String pictureFilePath, Set<MovieEntity> movies);
}
