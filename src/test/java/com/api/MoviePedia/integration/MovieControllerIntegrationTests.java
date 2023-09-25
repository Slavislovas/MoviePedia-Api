package com.api.MoviePedia.integration;

import com.api.MoviePedia.enumeration.Genre;
import com.api.MoviePedia.enumeration.Role;
import com.api.MoviePedia.model.movie.RatingCreationDto;
import com.api.MoviePedia.repository.MovieRepository;
import com.api.MoviePedia.repository.RatingRepository;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.repository.model.RatingEntity;
import com.api.MoviePedia.repository.model.UserEntity;
import com.api.MoviePedia.service.JWTService;
import com.api.MoviePedia.service.UserService;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MovieControllerIntegrationTests {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    JWTService jwtService;

    @MockBean
    UserService userService;

    @MockBean
    MovieRepository movieRepository;

    @MockBean
    RatingRepository ratingRepository;

    MovieEntity movieEntity;
    UserEntity userEntity;
    RatingEntity ratingEntity;

    @BeforeEach
    void init() throws Exception {
        movieEntity = new MovieEntity(1L, "TestTitle", "Test description", 2023, Genre.Action, "TestPath",
                "TestYoutubeUrl", 0, 0, 0.0, new DirectorEntity(), new HashSet<>(), new HashSet<>(), new HashSet<>(),
                new HashSet<>(), new HashSet<>());
        userEntity = new UserEntity(1L, "TestName", "TestSurname", LocalDate.now(), "TestEmail", "TestUsername",
                "TestPassword", Role.ROLE_USER, new HashSet<>(), new HashSet<>(), new HashSet<>());
        ratingEntity = new RatingEntity(1L, 1, userEntity, movieEntity);
    }

    @Test
    void addMovieToWatchedMovies_success() throws Exception {
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 1L, "role", "ROLE_USER"));
        mockMvc.perform(post("/movie/add/2/watched_movies/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isOk());
    }

    @Test
    void addMovieToWatchedMovies_fail_forbidden() throws Exception{
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 2L, "role", "ROLE_USER"));
        mockMvc.perform(post("/movie/add/2/watched_movies/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addMovieToWatchedMovies_fail_unauthorized() throws Exception {
        mockMvc.perform(post("/movie/add/2/watched_movies/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addMovieToWatchlist_success() throws Exception {
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 1L, "role", "ROLE_USER"));
        mockMvc.perform(post("/movie/add/2/watchlist/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isOk());
    }

    @Test
    void addMovieToWatchlist_fail_forbidden() throws Exception{
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 2L, "role", "ROLE_USER"));
        mockMvc.perform(post("/movie/add/2/watchlist/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addMovieToWatchlist_fail_unauthorized() throws Exception {
        mockMvc.perform(post("/movie/add/2/watchlist/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteMovieFromWatchedMovies_success() throws Exception {
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 1L, "role", "ROLE_USER"));
        mockMvc.perform(delete("/movie/delete/2/watched_movies/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMovieFromWatchedMovies_fail_forbidden() throws Exception{
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 2L, "role", "ROLE_USER"));
        mockMvc.perform(delete("/movie/delete/2/watched_movies/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteMovieFromWatchedMovies_fail_unauthorized() throws Exception {
        mockMvc.perform(delete("/movie/delete/2/watched_movies/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteMovieFromWatchlist_success() throws Exception {
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 1L, "role", "ROLE_USER"));
        mockMvc.perform(delete("/movie/delete/2/watchlist/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isOk());
    }

    @Test
    void  deleteMovieFromWatchlist_fail_forbidden() throws Exception{
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 2L, "role", "ROLE_USER"));
        mockMvc.perform(delete("/movie/delete/2/watchlist/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isForbidden());
    }

    @Test
    void  deleteMovieFromWatchlist_fail_unauthorized() throws Exception {
        mockMvc.perform(delete("/movie/delete/2/watchlist/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getWatchedMoviesByUserId_success() throws Exception {
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 1L, "role", "ROLE_USER"));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        mockMvc.perform(get("/movie/get/watched_movies/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isOk());
    }

    @Test
    void getWatchedMoviesByUserId_fail_forbidden() throws Exception {
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 1L, "role", "ROLE_USER"));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        mockMvc.perform(get("/movie/get/watched_movies/2")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getWatchedMoviesByUserId_fail_unauthorized() throws Exception {
        mockMvc.perform(get("/movie/get/watched_movies/2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getWatchlistByUserId_success() throws Exception {
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 1L, "role", "ROLE_USER"));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        mockMvc.perform(get("/movie/get/watchlist/1")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isOk());
    }

    @Test
    void getWatchlistByUserId_fail_forbidden() throws Exception {
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 1L, "role", "ROLE_USER"));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        mockMvc.perform(get("/movie/get/watchlist/2")
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getWatchlistByUserId_fail_unauthorized() throws Exception {
        mockMvc.perform(get("/movie/get/watchlist/2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rateMovieById_success() throws Exception {
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 1L, "role", "ROLE_USER"));
        Mockito.when(ratingRepository.save(any())).thenReturn(ratingEntity);
        JsonMapper jsonMapper = new JsonMapper();
        mockMvc.perform(post("/movie/rate")
                        .contentType("application/json")
                .content(jsonMapper.writeValueAsString(new RatingCreationDto(1, 1L, 1L)))
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isOk());
    }

    @Test
    void rateMovieById_fail_forbidden() throws Exception {
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movieEntity));
        Mockito.when(userService.getUserEntityById(any())).thenReturn(userEntity);
        Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
        Mockito.when(jwtService.validateToken(any())).thenReturn(Map.of("id", 2L, "role", "ROLE_USER"));
        Mockito.when(ratingRepository.save(any())).thenReturn(ratingEntity);
        JsonMapper jsonMapper = new JsonMapper();
        mockMvc.perform(post("/movie/rate")
                        .contentType("application/json")
                        .content(jsonMapper.writeValueAsString(new RatingCreationDto(1, 1L, 1L)))
                        .header("Authorization", "MockJWTToken"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rateMovieById_fail_unauthorized() throws Exception {
        JsonMapper jsonMapper = new JsonMapper();
        mockMvc.perform(post("/movie/rate")
                        .contentType("application/json")
                        .content(jsonMapper.writeValueAsString(new RatingCreationDto(1, 1L, 1L))))
                .andExpect(status().isUnauthorized());
    }
}
