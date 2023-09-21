package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.model.UserCreationDto;
import com.api.MoviePedia.model.UserEditDto;
import com.api.MoviePedia.model.UserRetrievalDto;
import com.api.MoviePedia.repository.UserRepository;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.UserService;
import com.api.MoviePedia.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public UserRetrievalDto registerUser(UserCreationDto creationDto) {
        Optional<UserEntity> optionalUserEntityByUsername = userRepository.findByUsername(creationDto.getUsername());
        if (optionalUserEntityByUsername.isPresent()){
            throw new DuplicateDatabaseEntryException("Username: " + creationDto.getUsername() + " is already taken");
        }
        Optional<UserEntity> optionalUserEntityByEmail = userRepository.findByEmail(creationDto.getEmail());
        if (optionalUserEntityByEmail.isPresent()){
            throw new DuplicateDatabaseEntryException("Email: " + creationDto.getEmail() + " is already taken");
        }
        creationDto.setPassword(bCryptPasswordEncoder.encode(creationDto.getPassword()));
        UserEntity userEntity = userMapper.creationDtoToEntity(creationDto, null, Role.ROLE_USER, new HashSet<>(), new HashSet<>(), new HashSet<>());
        return userMapper.entityToRetrievalDto(userRepository.save(userEntity));
    }

    @Override
    public UserRetrievalDto getLoggedInUserProfile() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        if (optionalUserEntity.isEmpty()){
            throw new NoSuchElementException("User with id: " + userId + " does not exist");
        }
        return userMapper.entityToRetrievalDto(optionalUserEntity.get());
    }

    @Override
    public UserRetrievalDto editLoggedInUserProfile(UserEditDto editDto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<UserEntity> optionalUserEntityByUsername = userRepository.findById(userId);
        if (optionalUserEntityByUsername.isEmpty()){
            throw new NoSuchElementException("User with id: " + userId + " does not exist");
        }
        Optional<UserEntity> optionalUserEntityByEmail = userRepository.findByEmail(editDto.getEmail());
        if (optionalUserEntityByEmail.isPresent() && ! optionalUserEntityByUsername.get().getEmail().equals(optionalUserEntityByEmail.get().getEmail())){
            throw new DuplicateDatabaseEntryException("Email: " + editDto.getEmail() + " is already taken");
        }
        UserEntity userEntity = userMapper.editDtoToEntity(editDto, optionalUserEntityByUsername.get().getId(), optionalUserEntityByUsername.get().getUsername(),
                                                            optionalUserEntityByUsername.get().getPassword(), optionalUserEntityByUsername.get().getRole(),
                                                            optionalUserEntityByUsername.get().getWatchlist(), optionalUserEntityByUsername.get().getWatchedMovies(),
                                                            optionalUserEntityByUsername.get().getReviews());
        return userMapper.entityToRetrievalDto(userRepository.save(userEntity));
    }

    @Override
    public UserEntity getUserEntityById(Long id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);
        if (optionalUserEntity.isEmpty()){
            throw new NoSuchElementException("User with username: " + id + " does not exist");
        }
        return optionalUserEntity.get();
    }
}
