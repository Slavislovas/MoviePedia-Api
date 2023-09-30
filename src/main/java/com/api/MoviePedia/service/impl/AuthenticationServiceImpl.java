package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.exception.InvalidLoginException;
import com.api.MoviePedia.repository.UserRepository;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.AuthenticationService;
import com.api.MoviePedia.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public String loginUser(String username, String password) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()){
            throw new InvalidLoginException("Invalid username or password");
        }

        if (!bCryptPasswordEncoder.matches(password, optionalUserEntity.get().getPassword())){
            throw new InvalidLoginException("Invalid username or password");
        }
        return jwtService.generateToken(optionalUserEntity.get().getId(), optionalUserEntity.get().getRole().name());
    }

    @Override
    public void checkIfUsernameIsAvailable(String username) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("Username: " + username + " is already taken");
        }
    }

    @Override
    public void checkIfEmailIsAvailable(String email) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("Email: " + email + " is already taken");
        }
    }
}
