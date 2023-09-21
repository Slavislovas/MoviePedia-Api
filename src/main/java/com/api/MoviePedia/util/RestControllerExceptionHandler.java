package com.api.MoviePedia.util;

import com.api.MoviePedia.exception.DuplicateDatabaseEntryException;
import com.api.MoviePedia.exception.ForeignKeyConstraintViolationException;
import com.api.MoviePedia.exception.InvalidLoginException;
import com.api.MoviePedia.exception.RequestBodyFieldValidationException;
import com.api.MoviePedia.model.ExceptionErrorModel;
import com.api.MoviePedia.model.FieldValidationErrorModel;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
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

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ExceptionErrorModel> handleInvalidLoginException(InvalidLoginException exception, HttpServletRequest request){
        return new ResponseEntity<>(new ExceptionErrorModel(LocalDateTime.now(), 400, exception.getMessage(), request.getServletPath()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value =ExpiredJwtException.class)
    public ResponseEntity<ExceptionErrorModel> handleExpiredJwtExceptionException(ExpiredJwtException exception, HttpServletRequest request){
        return new ResponseEntity<>(new ExceptionErrorModel(LocalDateTime.now(), 401, exception.getMessage(), request.getServletPath()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = MalformedJwtException.class)
    public ResponseEntity<ExceptionErrorModel> handleMalformedJwtExceptionException(MalformedJwtException exception, HttpServletRequest request){
        return new ResponseEntity<>(new ExceptionErrorModel(LocalDateTime.now(), 401, exception.getMessage(), request.getServletPath()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = UnsupportedJwtException.class)
    public ResponseEntity<ExceptionErrorModel> handleUnsupportedJwtExceptionException(UnsupportedJwtException exception, HttpServletRequest request){
        return new ResponseEntity<>(new ExceptionErrorModel(LocalDateTime.now(), 401, exception.getMessage(), request.getServletPath()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = SignatureException.class)
    public ResponseEntity<ExceptionErrorModel> handleSignatureExceptionException(SignatureException exception, HttpServletRequest request){
        return new ResponseEntity<>(new ExceptionErrorModel(LocalDateTime.now(), 401, exception.getMessage(), request.getServletPath()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<ExceptionErrorModel> handleIllegalStateExceptionException(IllegalStateException exception, HttpServletRequest request){
        return new ResponseEntity<>(new ExceptionErrorModel(LocalDateTime.now(), 401, exception.getMessage(), request.getServletPath()), HttpStatus.UNAUTHORIZED);
    }
}
