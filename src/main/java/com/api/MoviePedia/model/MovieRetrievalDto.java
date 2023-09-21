package com.api.MoviePedia.model;

import com.api.MoviePedia.enumeration.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MovieRetrievalDto {
    private Long id;
    private String title;
    private String description;
    private Integer year;
    private Genre genre;
    private Double rating;
    private String pictureFilePath;
    private String trailerYoutubeUrl;
    private DirectorRetrievalDto director;
    private Set<ActorRetrievalDto> actors;
}
