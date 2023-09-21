package com.api.MoviePedia.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExceptionErrorModel {
    private LocalDateTime timestamp;
    private Integer status;
    private String message;
    private String path;
}
