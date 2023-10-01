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
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Genre is required")
    private Genre genre;

    @NotEmpty(message = "Picture is required")
    @NotNull(message = "Picture is required")
    private byte[] picture;

    private String trailerYoutubeUrl;
}
