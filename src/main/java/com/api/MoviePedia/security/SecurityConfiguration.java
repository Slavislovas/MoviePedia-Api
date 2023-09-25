package com.api.MoviePedia.security;

import com.api.MoviePedia.security.filter.JWTTokenValidationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/movie/get/watchlist/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/movie/get/watched_movies/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/movie/add/**/watchlist/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/movie/add/**/watched_movies/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/movie/delete/**/watchlist/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/movie/delete/**/watched_movies/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/movie/rate").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/movie/create").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/movie/edit/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/movie/delete/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/review/get/**").permitAll()
                                .requestMatchers("/review/create").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/review/like").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/review/dislike").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/review/delete/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/movie/get/**").permitAll()
                                .requestMatchers("/movie/search").permitAll()
                                .requestMatchers("/director/get/**").permitAll()
                                .requestMatchers("/director/create").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/director/edit/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/director/delete/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/actor/get/**").permitAll()
                                .requestMatchers("/actor/create").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/actor/edit/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/actor/delete/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/user/register").permitAll()
                                .requestMatchers("/user/get/all").hasRole("ADMIN")
                                .requestMatchers("/user/get/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/user/delete/**").hasRole("ADMIN")
                                .requestMatchers("/user/edit/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/content_curator/get/all").hasRole("ADMIN")
                                .requestMatchers("/content_curator/get/**").hasAnyRole("CONTENT_CURATOR", "ADMIN")
                                .requestMatchers("/content_curator/create").hasRole("ADMIN")
                                .requestMatchers("/content_curator/edit/**").hasRole("ADMIN")
                                .requestMatchers("/content_curator/delete/**").hasRole("ADMIN")
                                .requestMatchers("/error").permitAll()
                                .anyRequest()
                                .authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
