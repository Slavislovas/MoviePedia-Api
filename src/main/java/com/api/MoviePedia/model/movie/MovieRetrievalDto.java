package com.api.MoviePedia.model.movie;

import com.api.MoviePedia.enumeration.Genre;
import com.api.MoviePedia.model.actor.ActorRetrievalDto;
import com.api.MoviePedia.model.director.DirectorRetrievalDto;
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
