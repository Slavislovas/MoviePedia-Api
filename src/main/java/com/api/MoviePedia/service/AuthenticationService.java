package com.api.MoviePedia.service;

import java.util.Map;

public interface AuthenticationService {
    Map<String, String> loginUser(String username, String password);

    Map<String, String> refreshAccessToken(String refreshToken);

    void checkIfUsernameIsAvailable(String username);

    void checkIfEmailIsAvailable(String email);

    void logoutUser();
}
