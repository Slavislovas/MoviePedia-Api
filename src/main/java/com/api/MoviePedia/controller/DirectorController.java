package com.api.MoviePedia.controller;

import com.api.MoviePedia.exception.RequestBodyFieldValidationException;
import com.api.MoviePedia.model.director.DirectorCreationDto;
import com.api.MoviePedia.model.director.DirectorRetrievalDto;
import com.api.MoviePedia.model.FieldValidationErrorModel;
import com.api.MoviePedia.service.DirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/director")
@RequiredArgsConstructor
@RestController
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("/get/all")
    public ResponseEntity<List<DirectorRetrievalDto>> getAllDirectors(){
        return ResponseEntity.ok(directorService.getAllDirectors());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<DirectorRetrievalDto> getDirectorById(@PathVariable("id") Long directorId){
        return new ResponseEntity<>(directorService.getDirectorById(directorId), HttpStatus.FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<DirectorRetrievalDto> createDirector(@RequestBody @Valid DirectorCreationDto directorCreationDto, BindingResult bindingResult) throws IOException {
        validateRequestBodyFields(bindingResult);
        return new ResponseEntity<>(directorService.createDirector(directorCreationDto), HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<DirectorRetrievalDto> editDirectorById(@PathVariable("id") Long directorId, @RequestBody @Valid DirectorCreationDto directorCreationDto,
                                                                 BindingResult bindingResult) throws IOException {
        validateRequestBodyFields(bindingResult);
        return ResponseEntity.ok(directorService.editDirectorById(directorId, directorCreationDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDirectorById(@PathVariable("id") Long directorId){
        directorService.deleteDirectorById(directorId);
        return ResponseEntity.ok().build();
    }

    private void validateRequestBodyFields(BindingResult bindingResult) {
        List<FieldValidationErrorModel> fieldValidationErrors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldValidationErrors.add(new FieldValidationErrorModel(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        if (!fieldValidationErrors.isEmpty()){
            throw new RequestBodyFieldValidationException(fieldValidationErrors);
        }
    }
}
