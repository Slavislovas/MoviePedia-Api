package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.model.UserCreationDto;
import com.api.MoviePedia.model.UserEditDto;
import com.api.MoviePedia.model.UserRetrievalDto;
import com.api.MoviePedia.repository.UserRepository;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.AuthenticationService;
import com.api.MoviePedia.service.UserService;
import com.api.MoviePedia.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationService authenticationService;
    @Override
    public UserRetrievalDto registerUser(UserCreationDto creationDto) {
        authenticationService.checkIfUsernameIsAvailable(creationDto.getUsername());
        authenticationService.checkIfEmailIsAvailable(creationDto.getEmail());
        creationDto.setPassword(bCryptPasswordEncoder.encode(creationDto.getPassword()));
        UserEntity userEntity = userMapper.creationDtoToEntity(creationDto, null, Role.ROLE_USER, new HashSet<>(), new HashSet<>(), new HashSet<>());
        return userMapper.entityToRetrievalDto(userRepository.save(userEntity));
    }

    @Override
    public List<UserRetrievalDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::entityToRetrievalDto).collect(Collectors.toList());
    }

    @Override
    public UserRetrievalDto getUserById(Long userId) {
        Role role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(x -> Role.valueOf(x.getAuthority())).toList().get(0);
        if (role == Role.ROLE_USER ){
            Long authenticatedUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!Objects.equals(authenticatedUserId, userId)){
                throw new SecurityException("Users can only view their own profile");
            }
        }
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        if (optionalUserEntity.isEmpty()){
            throw new NoSuchElementException("User with id: " + userId + " does not exist");
        }
        return userMapper.entityToRetrievalDto(optionalUserEntity.get());
    }

    @Override
    public UserRetrievalDto editUserById(UserEditDto editDto, Long userId) {
        Role role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(x -> Role.valueOf(x.getAuthority())).toList().get(0);
        if (role == Role.ROLE_USER ){
            Long authenticatedUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!Objects.equals(authenticatedUserId, userId)){
                throw new SecurityException("Users can only edit their own profile");
            }
        }
        Optional<UserEntity> optionalUserEntityById = userRepository.findById(userId);
        if (optionalUserEntityById.isEmpty()){
            throw new NoSuchElementException("User with id: " + userId + " does not exist");
        }
        Optional<UserEntity> optionalUserEntityByEmail = userRepository.findByEmail(editDto.getEmail());
        if (optionalUserEntityByEmail.isPresent() && ! optionalUserEntityById.get().getEmail().equals(optionalUserEntityByEmail.get().getEmail())){
            throw new DuplicateDatabaseEntryException("Email: " + editDto.getEmail() + " is already taken");
        }
        UserEntity userEntity = userMapper.editDtoToEntity(editDto, optionalUserEntityById.get().getId(), optionalUserEntityById.get().getUsername(),
                                                            optionalUserEntityById.get().getPassword(), optionalUserEntityById.get().getRole(),
                                                            optionalUserEntityById.get().getWatchlist(), optionalUserEntityById.get().getWatchedMovies(),
                                                            optionalUserEntityById.get().getReviews());
        return userMapper.entityToRetrievalDto(userRepository.save(userEntity));
    }

    @Override
    public UserEntity getUserEntityById(Long id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);
        if (optionalUserEntity.isEmpty()){
            throw new NoSuchElementException("User with id: " + id + " does not exist");
        }
        return optionalUserEntity.get();
    }

    @Override
    public void deleteUserById(Long userId) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        if (optionalUserEntity.isEmpty()){
            throw new NoSuchElementException("User with id: " + userId + " does not exist");
        }
        userRepository.deleteById(userId);
    }
}
