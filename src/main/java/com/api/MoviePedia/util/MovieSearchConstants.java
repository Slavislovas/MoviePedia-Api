package com.api.MoviePedia.util;

import java.util.List;

public final class MovieSearchConstants {
    public static final List<String> validMovieSearchFields = List.of("title", "year", "genre", "rating");
    public static final List<String> validDirectorSearchFields = List.of("name", "surname");
    public static final List<String> validActorSearchFields = List.of("name", "surname");
}
