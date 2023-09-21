package com.api.MoviePedia.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FieldValidationErrorModel {
    private String field;
    private String errorMessage;
}
