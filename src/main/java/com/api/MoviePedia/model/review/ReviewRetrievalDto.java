package com.api.MoviePedia.model.review;

import com.api.MoviePedia.model.UserRetrievalDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewRetrievalDto {
    private Long id;
    private String text;
    private Integer likes;
    private Integer dislikes;
    private String reviewerUsername;
}
