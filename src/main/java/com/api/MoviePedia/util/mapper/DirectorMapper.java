package com.api.MoviePedia.util.mapper;

import com.api.MoviePedia.model.director.DirectorCreationDto;
import com.api.MoviePedia.model.director.DirectorRetrievalDto;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.ImgurImageEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class DirectorMapper {
    @Mapping(target = "pictureFilePath", expression = "java(imgurImageLink)")
    public abstract DirectorRetrievalDto entityToRetrievalDto(DirectorEntity  entity, String imgurImageLink);

    @Mapping(target = "imgurImageEntity", expression = "java(imgurImageEntity)")
    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "movies", expression = "java(movies)")
    public abstract DirectorEntity creationDtoToEntity(DirectorCreationDto directorCreationDto, Long id, ImgurImageEntity imgurImageEntity, Set<MovieEntity> movies);
}
