package com.api.MoviePedia.util.mapper;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.model.UserEditDto;
import com.api.MoviePedia.model.contentcurator.ContentCuratorCreationDto;
import com.api.MoviePedia.model.contentcurator.ContentCuratorEditDto;
import com.api.MoviePedia.model.contentcurator.ContentCuratorRetrievalDto;
import com.api.MoviePedia.repository.model.ContentCuratorEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.repository.model.ReviewEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class ContentCuratorMapper {
    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "role", expression = "java(role)")
    public abstract ContentCuratorEntity creationDtoToEntity(ContentCuratorCreationDto creationDto, Long id, Role role);

    public abstract ContentCuratorRetrievalDto entityToRetrievalDto(ContentCuratorEntity entity);

    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "username", expression = "java(username)")
    @Mapping(target = "password", expression = "java(password)")
    @Mapping(target = "role", expression = "java(role)")
    public abstract ContentCuratorEntity editDtoToEntity(ContentCuratorEditDto editDto, Long id, String username, String password, Role role);
}
