package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.model.actor.ActorCreationDto;
import com.api.MoviePedia.model.actor.ActorRetrievalDto;
import com.api.MoviePedia.repository.ActorRepository;
import com.api.MoviePedia.repository.model.ActorEntity;
import com.api.MoviePedia.repository.model.ImgurImageEntity;
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
        return actorRepository.findAll().stream().map(actorEntity -> actorMapper.entityToRetrievalDto(actorEntity, actorEntity.getImgurImageEntity().getLink())).collect(Collectors.toList());
    }

    @Override
    public ActorRetrievalDto getActorById(Long actorId) {
        Optional<ActorEntity> optionalActorEntity = actorRepository.findById(actorId);
        if (optionalActorEntity.isEmpty()){
            throw new NoSuchElementException("Actor with id: " + actorId + " does not exist");
        }
        return actorMapper.entityToRetrievalDto(optionalActorEntity.get(), optionalActorEntity.get().getImgurImageEntity().getLink());
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
        ImgurImageEntity imgurImageEntity;
        if (actorCreationDto.getPicture().length != 0){
            imgurImageEntity = fileStorageService.saveFile(actorCreationDto.getPicture());
        } else {
            imgurImageEntity = new ImgurImageEntity();
        }
        ActorEntity actorEntity = actorMapper.creationDtoToEntity(actorCreationDto, null, imgurImageEntity, new HashSet<>());
        actorEntity = actorRepository.save(actorEntity);
        return actorMapper.entityToRetrievalDto(actorEntity, actorEntity.getImgurImageEntity().getLink());
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
        ImgurImageEntity imgurImageEntity;
        if (actorCreationDto.getPicture().length != 0){
            imgurImageEntity = editActorPicture(actorCreationDto, optionalActorEntityById.get());
        } else{
            imgurImageEntity = optionalActorEntityById.get().getImgurImageEntity();
        }

        ActorEntity actorEntity = actorMapper.creationDtoToEntity(actorCreationDto, actorId, imgurImageEntity, optionalActorEntityById.get().getMovies());
        actorEntity = actorRepository.save(actorEntity);
        return actorMapper.entityToRetrievalDto(actorEntity, actorEntity.getImgurImageEntity().getLink());
    }

    private ImgurImageEntity editActorPicture(ActorCreationDto newActorData, ActorEntity oldActorData) throws IOException {
        fileStorageService.deleteFileByHash(oldActorData.getImgurImageEntity().getId());
        return fileStorageService.saveFile(newActorData.getPicture());
    }

    @Override
    public void deleteActorById(Long actorId){
        Optional<ActorEntity> optionalActorEntity = actorRepository.findById(actorId);
        if (optionalActorEntity.isEmpty()){
            throw new NoSuchElementException("Actor with id: " + actorId + " does not exist");
        }
        ActorEntity actorEntity = optionalActorEntity.get();
        fileStorageService.deleteFileByHash(actorEntity.getImgurImageEntity().getId());
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
