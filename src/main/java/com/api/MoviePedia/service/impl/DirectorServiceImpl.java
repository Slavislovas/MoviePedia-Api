package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.exception.ForeignKeyConstraintViolationException;
import com.api.MoviePedia.model.DirectorCreationDto;
import com.api.MoviePedia.model.DirectorRetrievalDto;
import com.api.MoviePedia.repository.DirectorRepository;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.service.DirectorService;
import com.api.MoviePedia.service.FileStorageService;
import com.api.MoviePedia.util.ImageComparator;
import com.api.MoviePedia.util.mapper.DirectorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DirectorServiceImpl implements DirectorService {
    private final DirectorMapper directorMapper;
    private final DirectorRepository directorRepository;
    private final FileStorageService fileStorageService;

    @Override
    public List<DirectorRetrievalDto> getAllDirectors() {
        return directorRepository.findAll().stream().map(directorMapper::entityToRetrievalDto).collect(Collectors.toList());
    }

    @Override
    public DirectorRetrievalDto getDirectorById(Long directorId) {
        Optional<DirectorEntity> optionalDirectorEntity = directorRepository.findById(directorId);
        if (optionalDirectorEntity.isEmpty()){
            throw new NoSuchElementException("Director with id: " + directorId + " does not exist");
        }
        return directorMapper.entityToRetrievalDto(optionalDirectorEntity.get());
    }

    @Override
    public DirectorEntity getDirectorEntityById(Long directorId) {
        Optional<DirectorEntity> optionalDirectorEntity = directorRepository.findById(directorId);
        if (optionalDirectorEntity.isEmpty()){
            throw new NoSuchElementException("Director with id: " + directorId + " does not exist");
        }
        return optionalDirectorEntity.get();
    }

    @Override
    public DirectorRetrievalDto createDirector(DirectorCreationDto directorCreationDto) throws IOException {
        Optional<DirectorEntity> optionalDirectorEntity = directorRepository.findByNameAndSurname(directorCreationDto.getName(), directorCreationDto.getSurname());
        if (optionalDirectorEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("Director with name: " + directorCreationDto.getName() + " and surname: " + directorCreationDto.getSurname() + " already exists");
        }
        String imageFilePath = fileStorageService.saveFile(directorCreationDto.getPicture(), UUID.randomUUID().toString(), ".png");
        DirectorEntity directorEntity = directorMapper.creationDtoToEntity(directorCreationDto, null, imageFilePath, new HashSet<>());
        return directorMapper.entityToRetrievalDto(directorRepository.save(directorEntity));
    }

    @Override
    public DirectorRetrievalDto editDirectorById(Long directorId, DirectorCreationDto directorCreationDto) throws IOException {
        Optional<DirectorEntity> optionalDirectorEntityById = directorRepository.findById(directorId);
        if (optionalDirectorEntityById.isEmpty()){
            throw new NoSuchElementException("Director with id: " + directorId + " does not exist");
        }
        Optional<DirectorEntity> optionalDirectorEntityByNameAndSurname = directorRepository.findByNameAndSurname(directorCreationDto.getName(), directorCreationDto.getSurname());
        if (optionalDirectorEntityByNameAndSurname.isPresent() &&
                (!optionalDirectorEntityById.get().getName().equals(optionalDirectorEntityByNameAndSurname.get().getName()) &&
                        !optionalDirectorEntityById.get().getSurname().equals(optionalDirectorEntityByNameAndSurname.get().getSurname()))){
            throw new DuplicateDatabaseEntryException("Director with name: " + directorCreationDto.getName() + " and surname: " + directorCreationDto.getSurname() + " already exists");
        }
        String imageFilePath = editDirectorPicture(directorCreationDto, optionalDirectorEntityById.get());
        DirectorEntity directorEntity = directorMapper.creationDtoToEntity(directorCreationDto, directorId, imageFilePath, optionalDirectorEntityById.get().getMovies());
        return directorMapper.entityToRetrievalDto(directorRepository.save(directorEntity));
    }

    private String editDirectorPicture(DirectorCreationDto newDirectorData, DirectorEntity oldDirectorData) throws IOException {
        Boolean picturesAreTheSame = ImageComparator.areImagesEqual(newDirectorData.getPicture(), fileStorageService.retrieveFileContents(oldDirectorData.getPictureFilePath()));
        if (!picturesAreTheSame){
            fileStorageService.rewriteFileContents(oldDirectorData.getPictureFilePath(), newDirectorData.getPicture());
            return oldDirectorData.getPictureFilePath();
        }
        return oldDirectorData.getPictureFilePath();
    }

    @Override
    public void deleteDirectorById(Long directorId) {
        Optional<DirectorEntity> optionalDirectorEntity = directorRepository.findById(directorId);
        if (optionalDirectorEntity.isEmpty()){
            throw new NoSuchElementException("Director with id: " + directorId + " does not exist");
        }
        DirectorEntity directorEntity = optionalDirectorEntity.get();
        if (!directorEntity.getMovies().isEmpty()){
            StringJoiner stringJoiner = new StringJoiner(",");
            directorEntity.getMovies().stream().map(MovieEntity::getTitle).forEach(stringJoiner::add);
            throw new ForeignKeyConstraintViolationException("Cannot delete the director, because they are the director of the following movies: " +
                    stringJoiner.toString());
        }
        fileStorageService.deleteFileByPath(directorEntity.getPictureFilePath());
        directorRepository.deleteById(directorId);
    }
}
