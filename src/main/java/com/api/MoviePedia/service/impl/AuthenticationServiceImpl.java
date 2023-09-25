package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.exception.InvalidLoginException;
import com.api.MoviePedia.repository.AdminEntity;
import com.api.MoviePedia.repository.AdminRepository;
import com.api.MoviePedia.repository.ContentCuratorRepository;
import com.api.MoviePedia.repository.UserRepository;
import com.api.MoviePedia.repository.model.ContentCuratorEntity;
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
    private final ContentCuratorRepository contentCuratorRepository;
    private final AdminRepository adminRepository;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public String loginUser(String username, String password) {
//        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
//        if (optionalUserEntity.isEmpty()){
//            throw new InvalidLoginException("Invalid username or password");
//        }
//        UserEntity userEntity = optionalUserEntity.get();
//        if (!bCryptPasswordEncoder.matches(password, userEntity.getPassword())){
//            throw new InvalidLoginException("Invalid username or password");
//        }
//        return jwtService.generateToken(userEntity.getId(), userEntity.getRole().name());
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        Optional<ContentCuratorEntity> optionalContentCuratorEntity = contentCuratorRepository.findByUsername(username);
        Optional<AdminEntity> optionalAdminEntity = adminRepository.findByUsername(username);
        Long id;
        Role role;
        String entityPassword;
        if (optionalUserEntity.isPresent()){
            id = optionalUserEntity.get().getId();
            role = optionalUserEntity.get().getRole();
            entityPassword = optionalUserEntity.get().getPassword();
        } else if (optionalContentCuratorEntity.isPresent()){
            id = optionalContentCuratorEntity.get().getId();
            role = optionalContentCuratorEntity.get().getRole();
            entityPassword = optionalContentCuratorEntity.get().getPassword();
        } else if (optionalAdminEntity.isPresent()){
            id = optionalAdminEntity.get().getId();
            role = optionalAdminEntity.get().getRole();
            entityPassword = optionalAdminEntity.get().getPassword();
        } else{
            throw new InvalidLoginException("Invalid username or password");
        }

        if (!bCryptPasswordEncoder.matches(password, entityPassword)){
            throw new InvalidLoginException("Invalid username or password");
        }
        return jwtService.generateToken(id, role.name());
    }

    @Override
    public void checkIfUsernameIsAvailable(String username) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        Optional<ContentCuratorEntity> optionalContentCuratorEntity = contentCuratorRepository.findByUsername(username);
        if (optionalUserEntity.isPresent() || optionalContentCuratorEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("Username: " + username + " is already taken");
        }
    }

    @Override
    public void checkIfEmailIsAvailable(String email) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        Optional<ContentCuratorEntity> optionalContentCuratorEntity = contentCuratorRepository.findByEmail(email);
        if (optionalUserEntity.isPresent() || optionalContentCuratorEntity.isPresent()){
            throw new DuplicateDatabaseEntryException("Email: " + email + " is already taken");
        }
    }
}
