package com.api.MoviePedia.model.movie;

import com.api.MoviePedia.enumeration.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MovieCreationDto {
    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private Integer year;

    @NotNull
    private Genre genre;

    @NotEmpty
    @NotNull
    private byte[] picture;

    private String trailerYoutubeUrl;

    @NotNull
    private Long directorId;

    @NotEmpty
    @NotNull
    private Set<Long> actorIds;
}
