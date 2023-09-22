package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTServiceImpl implements JWTService {
    @Value("${jwt.expiration.time}")
    private Integer expirationTime;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Override
    public String generateToken(Long userId, String role) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setIssuer("MoviePedia")
                .setSubject(userId.toString())
                .claim("role", role)
                .setExpiration(new Date((new Date().getTime() + expirationTime)))
                .signWith(key)
                .compact();
    }

    @Override
    public Map<String, Object> validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", extractUserIdFromToken(token));
        claimsMap.put("role", extractRoleFromToken(token));
        return claimsMap;
    }

    @Override
    public Long extractUserIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong((String) claims.get("sub"));
    }

    @Override
    public String extractRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("role");
    }
}
