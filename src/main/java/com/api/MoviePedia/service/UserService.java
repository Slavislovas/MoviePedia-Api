package com.api.MoviePedia.service;

import com.api.MoviePedia.model.UserCreationDto;
import com.api.MoviePedia.model.UserEditDto;
import com.api.MoviePedia.model.UserRetrievalDto;
import com.api.MoviePedia.repository.model.UserEntity;

public interface UserService {
    UserRetrievalDto registerUser(UserCreationDto creationDto);

    UserRetrievalDto getUserById(Long userId);

    UserRetrievalDto editUserById(UserEditDto editDto, Long userId);

    UserEntity getUserEntityById(Long id);
}
