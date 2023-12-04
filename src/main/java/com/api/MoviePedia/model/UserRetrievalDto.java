package com.api.MoviePedia.model;

import com.api.MoviePedia.enumeration.Role;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRetrievalDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String email;
    private String username;
    private Role role;
}
