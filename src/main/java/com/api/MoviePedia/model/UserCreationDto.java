package com.api.MoviePedia.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreationDto {
    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String surname;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank
    @NotNull
    @Email
    private String email;

    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @NotNull
    @Pattern(regexp = "^(?=.*[a-zA-Z0-9]).*(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>?]).{8,}$",
            message = "Password must be at least 8 characters long, contain at least one special character and number")
    private String password;
}
