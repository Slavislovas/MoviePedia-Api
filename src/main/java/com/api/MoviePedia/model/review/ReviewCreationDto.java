package com.api.MoviePedia.model.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewCreationDto {
    @NotBlank
    @NotNull
    private String text;

    @NotNull
    private Long reviewerId;

    @NotNull
    private Long movieId;
}
