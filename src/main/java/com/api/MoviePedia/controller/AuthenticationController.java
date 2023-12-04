package com.api.MoviePedia.controller;

import com.api.MoviePedia.model.LoginCredentialsDto;
import com.api.MoviePedia.model.RefreshTokenRequest;
import com.api.MoviePedia.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginCredentialsDto loginCredentialsDto){
        return ResponseEntity.ok(authenticationService.loginUser(loginCredentialsDto.getUsername(), loginCredentialsDto.getPassword()));
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.refreshAccessToken(refreshTokenRequest.getRefreshToken()));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest){
        authenticationService.logoutUser(refreshTokenRequest);
        return ResponseEntity.ok("You have been successfully logged out!");
    }
}
