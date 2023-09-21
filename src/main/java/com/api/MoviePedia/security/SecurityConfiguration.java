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
                                .requestMatchers("/movie/get/**").permitAll()
                                .requestMatchers("/movie/search").permitAll()
                                .requestMatchers("/director/get/**").permitAll()
                                .requestMatchers("/actor/get/**").permitAll()
                                .requestMatchers("/user/register").permitAll()
                                .requestMatchers("/user/**").hasRole("USER")
                                .anyRequest()
                                .authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
