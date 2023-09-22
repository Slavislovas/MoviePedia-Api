package com.api.MoviePedia.model.movie;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RatingCreationDto {
    @Min(1)
    @Max(10)
    @NotNull
    private Integer rating;

    @NotNull
    private Long userId;

    @NotNull
    private Long movieId;
}
