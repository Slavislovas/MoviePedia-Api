package com.api.MoviePedia.model.review;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikeDislikeDto {
    @NotNull
    private Long userId;

    @NotNull
    private Long reviewId;
}
