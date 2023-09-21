package com.api.MoviePedia.security.filter;

import com.api.MoviePedia.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JWTTokenValidationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = request.getHeader("Authorization");
            token = token.replace("Bearer ", "");
            Map<String, Object> claims = jwtService.validateToken(token);
            Long id = (Long) claims.get("id");
            String role = (String) claims.get("role");
            Authentication authentication = new UsernamePasswordAuthenticationToken(id, null, AuthorityUtils.commaSeparatedStringToAuthorityList(role));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception exception){
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }
}
