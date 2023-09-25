package com.api.MoviePedia.service;

import java.util.Map;

public interface AuthenticationService {
    String loginUser(String username, String password);

    void checkIfUsernameIsAvailable(String username);

    void checkIfEmailIsAvailable(String email);
}
