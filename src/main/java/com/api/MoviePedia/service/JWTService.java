package com.api.MoviePedia.service;

import java.util.Map;

public interface JWTService {
    String generateToken(Long userId, String role);
    Map<String, Object> validateToken(String token);
    Long extractUserIdFromToken(String token);
    String extractRoleFromToken(String token);
}
