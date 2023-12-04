package com.api.MoviePedia.service;

import com.api.MoviePedia.model.FieldValidationErrorModel;
import com.api.MoviePedia.model.RefreshTokenRequest;

import java.util.List;
import java.util.Map;

public interface AuthenticationService {
    Map<String, String> loginUser(String username, String password);

    Map<String, String> refreshAccessToken(String refreshToken);

    void checkIfUsernameIsAvailable(String username, List<FieldValidationErrorModel> fieldErrors);

    void checkIfEmailIsAvailable(String email, List<FieldValidationErrorModel> fieldErrors);

    void logoutUser(RefreshTokenRequest refreshTokenRequest);
}
