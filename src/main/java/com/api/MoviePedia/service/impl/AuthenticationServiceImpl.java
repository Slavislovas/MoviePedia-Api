package com.api.MoviePedia.service.impl;

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
        UserEntity userEntity = optionalUserEntity.get();
        if (!bCryptPasswordEncoder.matches(password, userEntity.getPassword())){
            throw new InvalidLoginException("Invalid username or password");
        }
        return jwtService.generateToken(userEntity.getId(), userEntity.getRole().name());
    }
}
