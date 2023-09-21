package com.api.MoviePedia.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActorRetrievalDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String biography;
    private String pictureFilePath;
}
