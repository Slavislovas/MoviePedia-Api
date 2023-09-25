package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.model.contentcurator.ContentCuratorCreationDto;
import com.api.MoviePedia.model.contentcurator.ContentCuratorEditDto;
import com.api.MoviePedia.model.contentcurator.ContentCuratorRetrievalDto;
import com.api.MoviePedia.repository.ContentCuratorRepository;
import com.api.MoviePedia.repository.model.ContentCuratorEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.AuthenticationService;
import com.api.MoviePedia.service.ContentCuratorService;
import com.api.MoviePedia.util.mapper.ContentCuratorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContentCuratorServiceImpl implements ContentCuratorService {
    private final ContentCuratorRepository contentCuratorRepository;
    private final ContentCuratorMapper contentCuratorMapper;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public List<ContentCuratorRetrievalDto> getAllContentCurators() {
        return contentCuratorRepository.findAll().stream().map(contentCuratorMapper::entityToRetrievalDto).collect(Collectors.toList());
    }

    @Override
    public ContentCuratorRetrievalDto getContentCuratorById(Long contentCuratorId) {
        validateContentCuratorPermissions(contentCuratorId, "Content curators can only view their own profiles");
        Optional<ContentCuratorEntity> optionalContentCuratorEntity = contentCuratorRepository.findById(contentCuratorId);
        if (optionalContentCuratorEntity.isEmpty()){
            throw new NoSuchElementException("Content curator with id: " + contentCuratorId + " does not exist");
        }
        return contentCuratorMapper.entityToRetrievalDto(optionalContentCuratorEntity.get());
    }

    @Override
    public ContentCuratorRetrievalDto createContentCurator(ContentCuratorCreationDto creationDto) {
        authenticationService.checkIfUsernameIsAvailable(creationDto.getUsername());
        authenticationService.checkIfEmailIsAvailable(creationDto.getEmail());
        creationDto.setPassword(bCryptPasswordEncoder.encode(creationDto.getPassword()));
        ContentCuratorEntity contentCuratorEntity = contentCuratorMapper.creationDtoToEntity(creationDto, null, Role.ROLE_CONTENT_CURATOR);
        return contentCuratorMapper.entityToRetrievalDto(contentCuratorRepository.save(contentCuratorEntity));
    }

    @Override
    public ContentCuratorRetrievalDto editContentCuratorById(Long contentCuratorId, ContentCuratorEditDto editDto) {
        Optional<ContentCuratorEntity> optionalContentCuratorEntityById = contentCuratorRepository.findById(contentCuratorId);
        if (optionalContentCuratorEntityById.isEmpty()){
            throw new NoSuchElementException("Content curator with id: " + contentCuratorId + " does not exist");
        }
        Optional<ContentCuratorEntity> optionalContentCuratorEntityByEmail = contentCuratorRepository.findByEmail(editDto.getEmail());
        if (optionalContentCuratorEntityByEmail.isPresent() && ! optionalContentCuratorEntityById.get().getEmail().equals(optionalContentCuratorEntityByEmail.get().getEmail())){
            throw new DuplicateDatabaseEntryException("Email: " + editDto.getEmail() + " is already taken");
        }
        ContentCuratorEntity contentCuratorEntity = contentCuratorMapper.editDtoToEntity(editDto, optionalContentCuratorEntityById.get().getId(), optionalContentCuratorEntityById.get().getUsername(),
                optionalContentCuratorEntityById.get().getPassword(), optionalContentCuratorEntityById.get().getRole());
        return contentCuratorMapper.entityToRetrievalDto(contentCuratorRepository.save(contentCuratorEntity));
    }

    @Override
    public void deleteContentCuratorById(Long contentCuratorId) {
        Optional<ContentCuratorEntity> optionalContentCuratorEntity = contentCuratorRepository.findById(contentCuratorId);
        if (optionalContentCuratorEntity.isEmpty()){
            throw new NoSuchElementException("Content curator with id: " + contentCuratorId + " does not exist");
        }
        contentCuratorRepository.deleteById(contentCuratorId);
    }

    private void validateContentCuratorPermissions(Long contentCuratorId, String errorMessage) {
        Role role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(x -> Role.valueOf(x.getAuthority())).toList().get(0);
        if (role == Role.ROLE_CONTENT_CURATOR){
            Long authenticatedContentCuratorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!Objects.equals(authenticatedContentCuratorId, contentCuratorId)){
                throw new SecurityException(errorMessage);
            }
        }
    }
}
