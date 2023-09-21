package com.api.MoviePedia.service;

import com.api.MoviePedia.model.ActorCreationDto;
import com.api.MoviePedia.model.ActorRetrievalDto;
import com.api.MoviePedia.repository.model.ActorEntity;

import java.io.IOException;
import java.util.List;

public interface ActorService {
    List<ActorRetrievalDto> getAllActors();

    ActorRetrievalDto createActor(ActorCreationDto actorCreationDto) throws IOException;

    ActorRetrievalDto getActorById(Long actorId);

    ActorEntity getActorEntityById(Long actorId);

    void deleteActorById(Long actorId);

    ActorRetrievalDto editActorById(Long actorId, ActorCreationDto actorCreationDto) throws IOException;
}
