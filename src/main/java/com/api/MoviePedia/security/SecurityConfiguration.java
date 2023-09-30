package com.api.MoviePedia.security;

import com.api.MoviePedia.security.filter.JWTTokenValidationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {
    private final JWTTokenValidationFilter jwtTokenValidationFilter;
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenValidationFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authManagerRequestMatcherRegistry ->
                        authManagerRequestMatcherRegistry
                                .requestMatchers("/api/v1/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/movies").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/directors/**/movies/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/actors").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/actors/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/directors").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/directors/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/directors/**/movies/**/reviews").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/movies/search").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/users").hasAnyRole("ANONYMOUS", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/directors/**/movies/**/watched").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/directors/**/movies/**/watchlist").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/directors/**/movies/**/watched").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/directors/**/movies/**/watchlist").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/movies/watched").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/movies/watchlist").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/directors/**/movies/**/rate/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/directors/**/movies/**/reviews").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/directors/**/movies/**/reviews/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/directors/**/movies/**/reviews/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/directors/**/movies/**/reviews/**/likes").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/directors/**/movies/**/reviews/**/dislikes").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/actors").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/actors/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/actors/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/directors/**/movies").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/directors/**/movies/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/directors/**/movies/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/directors").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/directors/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/directors/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/content_curators").hasRole("ADMIN")
                                .requestMatchers("/error").permitAll()
                                .anyRequest()
                                .authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
