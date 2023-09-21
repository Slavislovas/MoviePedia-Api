package com.api.MoviePedia.util;

import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.exception.ForeignKeyConstraintViolationException;
import com.api.MoviePedia.exception.RequestBodyFieldValidationException;
import com.api.MoviePedia.model.ExceptionErrorModel;
import com.api.MoviePedia.model.FieldValidationErrorModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ExceptionHandler(RequestBodyFieldValidationException.class)
    public ResponseEntity<List<FieldValidationErrorModel>> handleRequestBodyFieldValidationException(RequestBodyFieldValidationException exception){
        return new ResponseEntity<>(exception.getFieldValidationErrors(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateDatabaseEntryException.class)
    public ResponseEntity<ExceptionErrorModel> handleDuplicateDatabaseEntryException(DuplicateDatabaseEntryException exception, HttpServletRequest request){
        return new ResponseEntity<>(new ExceptionErrorModel(LocalDateTime.now(), 409, exception.getMessage(), request.getServletPath()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionErrorModel> handleNoSuchElementException(NoSuchElementException exception, HttpServletRequest request){
        return new ResponseEntity<>(new ExceptionErrorModel(LocalDateTime.now(), 404, exception.getMessage(), request.getServletPath()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForeignKeyConstraintViolationException.class)
    public ResponseEntity<ExceptionErrorModel> handleForeignKeyConstraintViolationException(ForeignKeyConstraintViolationException exception, HttpServletRequest request){
        return new ResponseEntity<>(new ExceptionErrorModel(LocalDateTime.now(), 409, exception.getMessage(), request.getServletPath()), HttpStatus.CONFLICT);
    }
}
