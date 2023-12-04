package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.exception.InvalidLoginException;
import com.api.MoviePedia.exception.RequestBodyFieldValidationException;
import com.api.MoviePedia.model.FieldValidationErrorModel;
import com.api.MoviePedia.model.RefreshTokenRequest;
import com.api.MoviePedia.repository.UserRepository;
import com.api.MoviePedia.repository.model.RefreshTokenEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.AuthenticationService;
import com.api.MoviePedia.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public Map<String, String> loginUser(String username, String password) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()){
            throw new InvalidLoginException("Invalid username or password");
        }

        if (!bCryptPasswordEncoder.matches(password, optionalUserEntity.get().getPassword())){
            throw new InvalidLoginException("Invalid username or password");
        }
        String refreshToken;
        if (jwtService.checkIfRefreshTokenExistsByUserId(optionalUserEntity.get().getId())){
            refreshToken = jwtService.findRefreshTokenByUserId(optionalUserEntity.get().getId()).getToken();
        } else {
            refreshToken = jwtService.createRefreshToken(optionalUserEntity.get());
        }

        String accessToken = jwtService.generateAccessToken(optionalUserEntity.get().getId(), optionalUserEntity.get().getRole().name());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        return tokenMap;
    }

    @Override
    public Map<String, String> refreshAccessToken(String refreshToken){
        RefreshTokenEntity refreshTokenEntity = jwtService.findRefreshTokenByToken(refreshToken);
        jwtService.verifyRefreshTokenExpiration(refreshTokenEntity);
        String accessToken = jwtService.generateAccessToken(refreshTokenEntity.getUser().getId(), refreshTokenEntity.getUser().getRole().name());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        return tokenMap;
    }

    @Override
    public void checkIfUsernameIsAvailable(String username, List<FieldValidationErrorModel> fieldErrors) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isPresent()){
            fieldErrors.add(new FieldValidationErrorModel("username", "Username is taken"));
        }
    }

    @Override
    public void checkIfEmailIsAvailable(String email, List<FieldValidationErrorModel> fieldErrors) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isPresent()){
            fieldErrors.add(new FieldValidationErrorModel("email", "Email is taken"));
        }
    }

    @Override
    public void logoutUser(RefreshTokenRequest refreshTokenRequest) {
        jwtService.deleteRefreshTokenByToken(refreshTokenRequest);
    }
}
