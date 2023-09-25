package com.api.MoviePedia.model.contentcurator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContentCuratorRetrievalDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String email;
    private String username;
}
