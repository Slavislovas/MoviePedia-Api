package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.model.actor.ActorCreationDto;
import com.api.MoviePedia.model.actor.ActorRetrievalDto;
import com.api.MoviePedia.repository.ActorRepository;
import com.api.MoviePedia.repository.model.ActorEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.service.ActorService;
import com.api.MoviePedia.service.FileStorageService;
import com.api.MoviePedia.util.ImageComparator;
import com.api.MoviePedia.util.mapper.ActorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;
    private final FileStorageService fileStorageService;

    @Override
    public List<ActorRetrievalDto> getAllActors() {
        return actorRepository.findAll().stream().map(actorMapper::entityToRetrievalDto).collect(Collectors.toList());
    }

    @Override
    public ActorRetrievalDto getActorById(Long actorId) {
        Optional<ActorEntity> optionalActorEntity = actorRepository.findById(actorId);
        if (optionalActorEntity.isEmpty()){
            throw new NoSuchElementException("Actor with id: " + actorId + " does not exist");
        }
        return actorMapper.entityToRetrievalDto(optionalActorEntity.get());
    }

    @Override
    public ActorEntity getActorEntityById(Long actorId) {
        Optional<ActorEntity> optionalActorEntity = actorRepository.findById(actorId);
        if (optionalActorEntity.isEmpty()){
            throw new NoSuchElementException("Actor with id: " + actorId + " does not exist");
        }
        return optionalActorEntity.get();
    }

    @Override
    public ActorRetrievalDto createActor(ActorCreationDto actorCreationDto) throws IOException {
        Optional<ActorEntity> optionalActorEntity = actorRepository.findByNameAndSurname(actorCreationDto.getName(), actorCreationDto.getSurname());
        if (optionalActorEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("Actor with name: " + actorCreationDto.getName() + " and surname: " + actorCreationDto.getSurname() + " already exists");
        }
        String imageFilePath = fileStorageService.saveFile(actorCreationDto.getPicture(), UUID.randomUUID().toString(), ".png");
        ActorEntity actorEntity = actorMapper.creationDtoToEntity(actorCreationDto, null, imageFilePath, new HashSet<>());
        return actorMapper.entityToRetrievalDto(actorRepository.save(actorEntity));
    }

    @Override
    public ActorRetrievalDto editActorById(Long actorId, ActorCreationDto actorCreationDto) throws IOException {
       Optional<ActorEntity> optionalActorEntityById = actorRepository.findById(actorId);
        if (optionalActorEntityById.isEmpty()){
            throw new NoSuchElementException("Actor with id: " + actorId + " does not exist");
        }
        Optional<ActorEntity> optionalActorEntityByNameAndSurname = actorRepository.findByNameAndSurname(actorCreationDto.getName(), actorCreationDto.getSurname());
        if (optionalActorEntityByNameAndSurname.isPresent() &&
                (!optionalActorEntityById.get().getName().equals(optionalActorEntityByNameAndSurname.get().getName()) &&
                        !optionalActorEntityById.get().getSurname().equals(optionalActorEntityByNameAndSurname.get().getSurname()))){
            throw new DuplicateDatabaseEntryException("Actor with name: " + actorCreationDto.getName() + " and surname: " + actorCreationDto.getSurname() + " already exists");
        }
        String imageFilePath = editActorPicture(actorCreationDto, optionalActorEntityById.get());
        ActorEntity actorEntity = actorMapper.creationDtoToEntity(actorCreationDto, actorId, imageFilePath, optionalActorEntityById.get().getMovies());
        return actorMapper.entityToRetrievalDto(actorRepository.save(actorEntity));
    }

    private String editActorPicture(ActorCreationDto newActorData, ActorEntity oldActorData) throws IOException {
        Boolean picturesAreTheSame = ImageComparator.areImagesEqual(newActorData.getPicture(), fileStorageService.retrieveFileContents(oldActorData.getPictureFilePath()));
        if (!picturesAreTheSame){
            fileStorageService.rewriteFileContents(oldActorData.getPictureFilePath(), newActorData.getPicture());
            return oldActorData.getPictureFilePath();
        }
        return oldActorData.getPictureFilePath();
    }

    @Override
    public void deleteActorById(Long actorId){
        Optional<ActorEntity> optionalActorEntity = actorRepository.findById(actorId);
        if (optionalActorEntity.isEmpty()){
            throw new NoSuchElementException("Actor with id: " + actorId + " does not exist");
        }
        ActorEntity actorEntity = optionalActorEntity.get();
        fileStorageService.deleteFileByPath(actorEntity.getPictureFilePath());
        removeActorMovies(actorEntity);
        actorRepository.deleteById(actorId);
    }

    private void removeActorMovies(ActorEntity actorEntity) {
        for (Iterator<MovieEntity> iterator = actorEntity.getMovies().iterator(); iterator.hasNext();){
            MovieEntity movieEntity = iterator.next();
            iterator.remove();
            movieEntity.getActors().remove(actorEntity);
        }
    }
}
