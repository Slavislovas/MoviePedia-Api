package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.model.director.DirectorCreationDto;
import com.api.MoviePedia.model.director.DirectorRetrievalDto;
import com.api.MoviePedia.repository.DirectorRepository;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.ImgurImageEntity;
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
        return directorRepository.findAll().stream().map(directorEntity -> directorMapper.entityToRetrievalDto(directorEntity, directorEntity.getImgurImageEntity().getLink())).collect(Collectors.toList());
    }

    @Override
    public DirectorRetrievalDto getDirectorById(Long directorId) {
        Optional<DirectorEntity> optionalDirectorEntity = directorRepository.findById(directorId);
        if (optionalDirectorEntity.isEmpty()){
            throw new NoSuchElementException("Director with id: " + directorId + " does not exist");
        }
        return directorMapper.entityToRetrievalDto(optionalDirectorEntity.get(), optionalDirectorEntity.get().getImgurImageEntity().getLink());
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
        ImgurImageEntity imgurImageEntity = fileStorageService.saveFile(directorCreationDto.getPicture());
        DirectorEntity directorEntity = directorMapper.creationDtoToEntity(directorCreationDto, null, imgurImageEntity, new HashSet<>());
        directorEntity = directorRepository.save(directorEntity);
        return directorMapper.entityToRetrievalDto(directorEntity, directorEntity.getImgurImageEntity().getLink());
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
        ImgurImageEntity imgurImageEntity = editDirectorPicture(directorCreationDto, optionalDirectorEntityById.get());
        DirectorEntity directorEntity = directorMapper.creationDtoToEntity(directorCreationDto, directorId, imgurImageEntity, optionalDirectorEntityById.get().getMovies());
        directorEntity = directorRepository.save(directorEntity);
        return directorMapper.entityToRetrievalDto(directorEntity, directorEntity.getImgurImageEntity().getLink());
    }

    private ImgurImageEntity editDirectorPicture(DirectorCreationDto newDirectorData, DirectorEntity oldDirectorData) throws IOException {
       fileStorageService.deleteFileByHash(oldDirectorData.getImgurImageEntity().getId());
       return fileStorageService.saveFile(newDirectorData.getPicture());
    }

    @Override
    public void deleteDirectorById(Long directorId) {
        Optional<DirectorEntity> optionalDirectorEntity = directorRepository.findById(directorId);
        if (optionalDirectorEntity.isEmpty()){
            throw new NoSuchElementException("Director with id: " + directorId + " does not exist");
        }
        DirectorEntity directorEntity = optionalDirectorEntity.get();
        fileStorageService.deleteFileByHash(directorEntity.getImgurImageEntity().getId());
        for (MovieEntity movie : directorEntity.getMovies()) {
            fileStorageService.deleteFileByHash(movie.getImgurImageEntity().getId());
        }
        directorRepository.deleteById(directorId);
    }
}
